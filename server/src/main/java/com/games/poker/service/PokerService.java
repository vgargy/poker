package com.games.poker.service;


import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.games.poker.dto.Direction;
import com.games.poker.dto.GameInfo;
import com.games.poker.dto.PlayerAmount;
import com.games.poker.dto.PlayerInfo;
import com.games.poker.dto.TransactionInfo;
import com.games.poker.exceptions.NotFoundException;
import com.games.poker.model.Game;
import com.games.poker.model.Player;
import com.games.poker.model.PlayerGame;
import com.games.poker.model.Status;
import com.games.poker.model.Transaction;
import com.games.poker.persistence.GameRepository;
import com.games.poker.persistence.PlayerGameRepository;
import com.games.poker.persistence.PlayerRepository;
import com.games.poker.persistence.TransactionRepository;

@Service
public class PokerService {
	
	private static final String PAY_TO = "pay to %s";
	private static final String GET_FROM = "get from %s";
	
    private static final Logger logger = LogManager.getLogger(PokerService.class);


	@Autowired
	GameRepository gameRepository;

	@Autowired
	PlayerRepository playerRepository;

	@Autowired
	PlayerGameRepository playerGameRepository;

	@Autowired
	TransactionRepository transactionRepository;

	public UUID newGame(BigDecimal buyIn, String venue, String dateStr) {

		Date date = Date.valueOf(LocalDate.now());
		if(isNotBlank(dateStr)) {
			LocalDate localDate = LocalDate.parse(dateStr);
			date = Date.valueOf(localDate);
		}

		Game game = new Game();
		game.setBuyIn(buyIn);
		game.setVenue(venue);
		game.setDate(date);
		game.setStatus(Status.New);
		game = gameRepository.save(game);

		return game.getId();
	}


	public GameInfo addNewPlayer(String firstName, String lastName, UUID gameId) {
		Player player = playerRepository.find(firstName, lastName);
		if(player == null) {
			player = new Player();
			player.setFirstName(firstName);
			player.setLastName(lastName);
			player = playerRepository.save(player);
		}
		addPlayer(gameId, player.getId());
		return getGameDetails(gameId);
	}

	public GameInfo addPlayer(UUID gameId, UUID playerId) {

		if(playerGameRepository.exists(gameId, playerId)) {
			return getGameDetails(gameId);
		}

		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}

		Player player = playerRepository.find(playerId);
		if(player == null) {
			throw new NotFoundException("player with id : %s not found".formatted(playerId.toString()));
		}

		PlayerGame playerGame = new PlayerGame();
		playerGame.setBuyIns(game.getBuyIn());
		playerGame.setGame(game);
		playerGame.setPlayer(player);
		playerGameRepository.save(playerGame);
		return getGameDetails(gameId);
	}

	public void reBuy(UUID gameId, UUID playerId, BigDecimal amount) {
		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}

		Player player = playerRepository.find(playerId);
		if(player == null) {
			throw new NotFoundException("player with id : %s not found".formatted(playerId.toString()));
		}
		playerGameRepository.addRebuy(game.getId(), player.getId(), amount);
	}

	public List<PlayerInfo> allPlayers() {

		return playerRepository.findAll()
				.stream()
				.map(p -> new PlayerInfo(
						p.getId(),
						p.getName()))
				.toList();
	}

	public List<GameInfo> allGames() {
		return gameRepository.findAll()
				.stream()
				.map(g -> new GameInfo(
						g.getId(),
						g.getDate(),
						g.getVenue(),
						g.getStatus()
						))
				.toList();

	}

	public GameInfo getGameDetails(UUID gameId) {

		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}

		List<PlayerGame> playerGames = playerGameRepository.getGameDetails(gameId);

		List<PlayerInfo> players = playerGames
				.stream()
				.map(p -> new PlayerInfo(
						p.getPlayer().getId(),
						p.getPlayer().getName(),
						p.getBuyIns(),
						p.getCredit(),
						p.getCashIn()))
				.toList();

		return new GameInfo(
				game.getId(),
				game.getDate(),
				game.getVenue(),
				game.getStatus(),
				game.getBuyIn(),
				players);
	}

	public void credit(UUID gameId, UUID playerId, BigDecimal amount) {
		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}

		Player player = playerRepository.find(playerId);
		if(player == null) {
			throw new NotFoundException("player with id : %s not found".formatted(playerId.toString()));
		}
		playerGameRepository.setCredit(game.getId(), player.getId(), amount);

	}


	public void cashIn(UUID gameId, UUID playerId, BigDecimal amount) {
		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}

		Player player = playerRepository.find(playerId);
		if(player == null) {
			throw new NotFoundException("player with id : %s not found".formatted(playerId.toString()));
		}
		playerGameRepository.setCashIn(game.getId(), player.getId(), amount);

	}


	public File generate(UUID gameId) throws Exception {
		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}
		
		String date = DateFormatUtils.format(game.getDate(), "yyyy-MM-dd");

		boolean hasCreditExist = playerGameRepository.existsCredit(gameId);
		List<String> headers = List.of("Name", date);
		if(hasCreditExist) {
			 headers = List.of("Name", date, "Miscellaneous", "Total");
		}
		int maxColIdx = headers.size() - 1;



		String fileName = "%s.xlsx".formatted(date);
		File file = new File("/tmp", fileName);
		try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {

			Sheet sheet = workbook.createSheet();

			int cellId = 0;
			Row headerRow = sheet.createRow(0);
			for (String header : headers) {
				sheet.setColumnWidth(cellId, 5000);
				headerRow.createCell(cellId++)
				.setCellValue(new HSSFRichTextString(header));
			}

			int rowIdx = 1;
			BigDecimal totalAmount = new BigDecimal(0.0);
			List<PlayerAmount> positives = new ArrayList<>();
			List<PlayerAmount> negatives = new ArrayList<>();
			for(PlayerGame row : playerGameRepository.getGameDetails(gameId)) { 

				Player player = row.getPlayer();
				Row playerRow = sheet.createRow(rowIdx++);
				playerRow.createCell(0).setCellValue(new HSSFRichTextString(player.getName()));

				BigDecimal buyIns = row.getBuyIns();
				BigDecimal cashIn = row.getCashIn();
				BigDecimal credit  = row.getCredit();

				BigDecimal amount = cashIn.add(credit).subtract(buyIns);
				totalAmount = totalAmount.add(amount);
				PlayerAmount pa = new PlayerAmount(player, amount.abs());
				if(amount.compareTo(BigDecimal.ZERO) >=0) {
					positives.add(pa);
				} else {
					negatives.add(pa);
				}
				if(credit.compareTo(BigDecimal.ZERO) == 0) {
					playerRow.createCell(1).setCellValue(new HSSFRichTextString(amount.toPlainString()));
				} else {
					BigDecimal pokerAmount  = cashIn.subtract(buyIns);
					playerRow.createCell(1).setCellValue(new HSSFRichTextString(pokerAmount.toPlainString()));
					playerRow.createCell(2).setCellValue(new HSSFRichTextString(credit.toPlainString()));
					playerRow.createCell(3).setCellValue(new HSSFRichTextString(amount.toPlainString()));

				}
			}

			int lastRow = rowIdx;

			Row sumRow = sheet.createRow(rowIdx++);
			sumRow.createCell(maxColIdx).setCellValue(new HSSFRichTextString(totalAmount.toPlainString()));

			//write worksheet
			int idxWkSheet = printWorksheet(sheet, positives, negatives, maxColIdx);

			//print transactions
			printTransactions(game, sheet, idxWkSheet > lastRow ? idxWkSheet : lastRow, maxColIdx);


			//write to File
			try (FileOutputStream out = new FileOutputStream(file)) {
				workbook.write(out);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error writing file for entityName: %s".formatted(date));
		}
		return file;


	}

	private void printTransactions(Game game, Sheet sheet, int idx, int startColIdx) {
		idx = idx+3;
		Row startRow = getRow(sheet, idx);
		startRow.createCell(startColIdx + 1).setCellValue(new HSSFRichTextString("From"));
		startRow.createCell(startColIdx + 2).setCellValue(new HSSFRichTextString("To"));
		startRow.createCell(startColIdx + 3).setCellValue(new HSSFRichTextString("Amount"));
		startRow.createCell(startColIdx  +4).setCellValue(new HSSFRichTextString("Dues Cleared"));

		int _idx = idx;
		for(Transaction tx : game.getTransactions()) {
			Row trxRow = getRow(sheet, ++_idx);
			
			int _startColIdx = startColIdx +1;
			trxRow.createCell(_startColIdx).setCellValue(new HSSFRichTextString(tx.getPayer().getName()));
			sheet.setColumnWidth(_startColIdx, 5000);

			_startColIdx++;
			trxRow.createCell(_startColIdx).setCellValue(new HSSFRichTextString(tx.getReceiver().getName()));
			sheet.setColumnWidth(_startColIdx, 5000);

			_startColIdx++;
			trxRow.createCell(_startColIdx).setCellValue(new HSSFRichTextString(tx.getAmount().toPlainString()));
			sheet.setColumnWidth(_startColIdx, 5000);

			_startColIdx++;
			trxRow.createCell(_startColIdx).setCellValue(new HSSFRichTextString(""));
			sheet.setColumnWidth(_startColIdx, 5000);
		}

	}

	private int printWorksheet(Sheet sheet, Collection<PlayerAmount> positives, Collection<PlayerAmount> negatives, int startColIdx) {
		
		int posStartColIdx = startColIdx + 1;
		Row workSheetRow = sheet.getRow(3);
		workSheetRow.createCell(posStartColIdx).setCellValue(new HSSFRichTextString("Worksheet"));
		getRow(sheet, 4).createCell(posStartColIdx).setCellValue(new HSSFRichTextString("Positive"));
		int idx = 5;

		BigDecimal totalPositive = BigDecimal.ZERO;
		for(PlayerAmount positive : positives) {
			Row rx = getRow(sheet,idx++);
			sheet.setColumnWidth(posStartColIdx, 5000);
			rx.createCell(posStartColIdx).setCellValue(new HSSFRichTextString(positive.player().getName()));
			totalPositive = totalPositive.add(positive.amount());
			rx.createCell(posStartColIdx + 1).setCellValue(new HSSFRichTextString(positive.amount().toPlainString()));

		}
		int posIdx = idx;

		int negstartColIdx = posStartColIdx + 3;
		BigDecimal totalNegative = BigDecimal.ZERO;
		sheet.getRow(4 ).createCell(negstartColIdx).setCellValue(new HSSFRichTextString("Negative"));
		idx = 5;
		for(PlayerAmount negative : negatives) {
			Row rx = getRow(sheet,idx++);
			sheet.setColumnWidth(negstartColIdx, 5000);
			rx.createCell(negstartColIdx).setCellValue(new HSSFRichTextString(negative.player().getName()));
			totalNegative = totalNegative.add(negative.amount());
			rx.createCell(negstartColIdx + 1).setCellValue(new HSSFRichTextString(negative.amount().toPlainString()));
		}
		int negIdx = idx;

		int totIdx = negIdx > posIdx ? negIdx : posIdx;
		Row totalRow = getRow(sheet,totIdx);
		totalRow.createCell(posStartColIdx + 1).setCellValue(new HSSFRichTextString(totalPositive.toPlainString()));
		totalRow.createCell(negstartColIdx + 1).setCellValue(new HSSFRichTextString(totalNegative.toPlainString()));
		return totIdx;
	}

	private Row getRow(Sheet sheet, int idx) {
		Row row = sheet.getRow(idx);
		return row != null ? row : sheet.createRow(idx);
	}


	public void settle(UUID gameId) {
		Game game = gameRepository.find(gameId);
		if(game == null) {
			throw new NotFoundException("game with id : %s not found".formatted(gameId.toString()));
		}

		//delete existing transactions
		transactionRepository.deleteTransactions(gameId);
		
		List<PlayerAmount> positives = new ArrayList<>();
		List<PlayerAmount> negatives = new ArrayList<>();
		for(PlayerGame row : game.getPlayerGames()) { 

			Player player = row.getPlayer();
			BigDecimal buyIns = row.getBuyIns();
			BigDecimal cashIn = row.getCashIn();
			BigDecimal credit  = row.getCredit();

			BigDecimal amount = cashIn.add(credit).subtract(buyIns);
			PlayerAmount pa = new PlayerAmount(player, amount.abs());
			if(amount.compareTo(BigDecimal.ZERO) >=0) {
				positives.add(pa);
			} else {
				negatives.add(pa);
			}
		}

		LinkedList<PlayerAmount> posQueue = new LinkedList<>(positives);
		posQueue.sort((a, b) -> b.amount().compareTo(a.amount()));

		LinkedList<PlayerAmount> negQueue = new LinkedList<>(negatives);
		negQueue.sort((a, b) -> b.amount().compareTo(a.amount()));

		int displayOrder = 0;
		while (!posQueue.isEmpty() && !negQueue.isEmpty()) {
			PlayerAmount receiver = posQueue.pollFirst();
			PlayerAmount payer = negQueue.pollFirst();

			BigDecimal transfer = receiver.amount().min(payer.amount());

			//if transaction does not exist, insert
			if(!transactionRepository.exists(payer.player().getId(),
					receiver.player().getId(), game.getId())) {
				logger.info("Creating transaction between {} -> {} amount:{} with Index:{}",
						payer.player().getName(), receiver.player().getName(), transfer, displayOrder);
				Transaction transaction  = new Transaction();
				transaction.setAmount(transfer);
				transaction.setDuesCleared(false);
				transaction.setGame(game);
				transaction.setPayer(payer.player());
				transaction.setReceiver(receiver.player());
				transaction.setDisplayOrder(displayOrder++);

				transactionRepository.save(transaction);
			}


			BigDecimal receiverLeft = receiver.amount().subtract(transfer);
			BigDecimal payerLeft = payer.amount().subtract(transfer);

			if (receiverLeft.compareTo(BigDecimal.ZERO) > 0) {
				posQueue.addFirst(new PlayerAmount(receiver.player(), receiverLeft));
			}

			if (payerLeft.compareTo(BigDecimal.ZERO) > 0) {
				negQueue.addFirst(new PlayerAmount(payer.player(), payerLeft));
			}
		}

		game.setStatus(Status.Settled);
		gameRepository.save(game);


	}


	public PlayerInfo getPlayerDetails(UUID playerId) {
		Player player = playerRepository.find(playerId);
		if(player == null) {
			throw new NotFoundException("player with id : %s not found".formatted(playerId.toString()));
		}
		PlayerInfo info = new PlayerInfo(player.getId(), player.getName());
		List<TransactionInfo> transactions = new ArrayList<>();
		info.setTransactions(transactions);
		
		//In
		for(Transaction transaction : player.getMoneyIn()) {
			transactions.add(
					new TransactionInfo(transaction.getId(),
							GET_FROM.formatted(transaction.getPayer().getName()),
							transaction.getAmount(),		
							Direction.CREDIT,
							transaction.isDuesCleared(),
							transaction.getGame().getDate()));
		}	
		
		//out
		for(Transaction transaction : player.getMoneyOut()) {
			transactions.add(
					new TransactionInfo(transaction.getId(),
							PAY_TO.formatted(transaction.getReceiver().getName()),
							transaction.getAmount(),		
							Direction.DEBIT,
							transaction.isDuesCleared(),
							transaction.getGame().getDate()));

		}	
		
		
		return info;
	}

}
