import { useEffect, useState } from "react";
import {
  Table,
  Button,
  Dropdown,
  Input,
  IconButton,
  toaster,
  Message,
  Whisper,
  Popover,
  Stack
} from "rsuite";

import CreatableSelect from "react-select/creatable";
import { PiPokerChipThin } from "react-icons/pi";
import { GoPlus } from "react-icons/go";
import { SlOptionsVertical } from "react-icons/sl";
import Menu from "../shared/Menu";
import MenuItem from "../shared/MenuItem";


const { Column, HeaderCell, Cell } = Table;

export default function Game({ gameId, onBack }) {
  const [players, setPlayers] = useState([]);
  const [data, setData] = useState(null);
  const [allPlayers, setAllPlayers] = useState([]);
  const [activeRowId, setActiveRowId] = useState(null);
  const [amount, setAmount] = useState({});
  const [settle, setSettle] = useState(false);

  useEffect(() => {
    loadGame();
  }, [gameId]);

  useEffect(() => {
    listPlayers();
  }, []);

  const settleGame = async () => {
    setSettle(true);
  }
  const loadGame = async () => {
    const res = await fetch(`/poker/game/${gameId}`);
    const data = await res.json();
    setPlayers(data.players || []);
    setData(data);
  };

  const listPlayers = async () => {
    const res = await fetch("/poker/players");
    const data = await res.json();

    setAllPlayers(
      data.map(p => ({
        label: `${p.firstName} ${p.lastName}`,
        value: p.id
      }))
    );
  };

  const generateGame = async() => {
      try {
        const response = await fetch(`/poker/${gameId}/generate`);
        if (!response.ok) {
          throw new Error("Download failed");
        }

        const blob = await response.blob();

        // Create a temporary download link
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");

        let fileName = "poker-file.xlsx";
        const contentDisposition = response.headers.get("content-disposition");
        if (contentDisposition) {
          const match = contentDisposition.match(/filename="?([^"]+)"?/);
          if (match?.[1]) {
            fileName = match[1];
          }
        }
        a.href = url;
        a.download = fileName; // change filename if needed
        document.body.appendChild(a);
        a.click();

        // Cleanup
        a.remove();
        window.URL.revokeObjectURL(url);
      }  catch (err) {
      toaster.push(
        <Message type="error">Rebuy failed</Message>,
        { placement: "topEnd" }
      );
    }
  }

  const renderTotalAmount = (rowData) =>
    (rowData.cashIn+rowData.credit) - rowData.buyIns;

  const submitAmount = async (playerId, action) => {
    const pAmount = amount !== null ? amount[playerId]?.[action] : null;
    if(pAmount === null || pAmount === undefined) {
      return;
    }
    try {
      console.log('playerId:', playerId, 'Amount:', pAmount, 'action', action);
      await fetch(`/poker/${gameId}/player/${playerId}/${action}/${pAmount}`, {
        method: "POST"
      });

      setActiveRowId(null);
      setAmount({});
      loadGame();
    } catch (err) {
      toaster.push(
        <Message type="error">Rebuy failed</Message>,
        { placement: "topEnd" }
      );
    }
  };

  const renderName = (rowData) =>
    `${rowData.firstName} ${rowData.lastName}`;

  return (
    <div style={{ padding: 20 }}>
      <Button onClick={onBack} style={{ marginBottom: 10 }}>
        Back to Games
      </Button>

       <Stack justifyContent="space-between">
        <div></div>
        <Menu menuItems={[<MenuItem name="settle" label={"Settle"} action={settleGame}/>]}/>
       </Stack>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "120px 1fr",
          gap: 8,
          marginBottom: 20
        }}
      >
        <strong>Venue</strong>
        <div>{data?.venue}</div>

        <strong>Date</strong>
        <div>{data?.date}</div>

        <strong>Buy In</strong>
        <div>{data?.buyIn}</div>
      </div>

      <CreatableSelect
        isClearable
        placeholder="Select or add player"
        options={allPlayers}
        menuPortalTarget={document.body}
        styles={{
          container: base => ({ ...base, width: 300 }),
          menuPortal: base => ({ ...base, zIndex: 9999 })
        }}
      />

      <Table height={300} data={players}>
        <Column width={155}>
          <HeaderCell>Name</HeaderCell>
          <Cell>{rowData => renderName(rowData)}</Cell>
        </Column>

        <Column width={100} align="center">
          <HeaderCell>Buy Ins</HeaderCell>
          <Cell dataKey="buyIns" />
        </Column>

        {settle &&
        <>
        <Column width={110} >
          <HeaderCell>Credit</HeaderCell>
          <Cell>
             {(rowData) => (
              <>
                <Input
                  type="number"
                  placeholder="Credit"
                  value={amount[rowData?.id]?.credit || rowData.credit}
                  onChange={(value) =>
                    setAmount((prev) => ({
                      ...prev,
                      [rowData.id]: { ...prev[rowData.id], credit: value },
                    }))
                  }
                  style={{ width: 90 }}
                  onBlur={() => {
                    const value = amount[rowData.id]?.credit;
                    if (value) submitAmount(rowData.id, "credit");
                  }}
                />
              </>
              )}
          </Cell>
        </Column>

        <Column width={110}>
          <HeaderCell>Cash In</HeaderCell>
          <Cell>
             {(rowData) => (
              <>
                <Input
                  type="number"
                  placeholder="Cash In"
                  value={amount[rowData?.id]?.cashIn || rowData.cashIn}
                  onChange={(value) =>
                    setAmount((prev) => ({
                      ...prev,
                      [rowData.id]: { ...prev[rowData.id], cashIn: value },
                    }))
                  }
                  style={{ width: 90 }}
                  onBlur={() => {
                    const value = amount[rowData.id]?.cashIn;
                    if (value) submitAmount(rowData.id, "cashIn");
                  }}
                />
              </>
              )}
          </Cell>
        </Column>
        <Column width={155}>
          <HeaderCell>Amount</HeaderCell>
          <Cell>{rowData => renderTotalAmount(rowData)}</Cell>
        </Column>
        </>
        }
        {!settle &&
        <Column width={60} align="center" fixed="right">
          <HeaderCell />
          <Cell>
            {(rowData) => (
              <>
                {/* 3-dot menu */}
                <Whisper
                  placement="bottomEnd"
                  trigger="click"
                  speaker={({ onClose, left, top, className }, ref) => (
                    <Popover
                      ref={ref}
                      className={className}
                      style={{ left, top }}
                      full
                    >
                      <Dropdown.Menu>
                        <Dropdown.Item
                          icon={<PiPokerChipThin />}
                          onClick={() => {
                            setActiveRowId(rowData.id);
                            onClose();
                          }}
                        >
                          Rebuy
                        </Dropdown.Item>
                      </Dropdown.Menu>
                    </Popover>
                  )}
                >
                  <IconButton
                    appearance="subtle"
                    icon={<SlOptionsVertical />}
                  />
                </Whisper>

                {/* Amount popup */}
                <Whisper
                  trigger="manual"
                  placement="left"
                  open={activeRowId === rowData.id}
                  rootClose
                  onClose={() => {
                    setActiveRowId(null);
                    setAmount({});
                  }}
                  speaker={
                    <Popover style={{ padding: 10 }}>
                      <div
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: 8
                        }}
                      >
                        <Input
                          type="number"
                          placeholder="Amount"
                          value={amount[rowData?.id]?.reBuy || ""}
                          onChange={(value) =>
                            setAmount((prev) => ({
                              ...prev,
                              [rowData.id]: { ...prev[rowData.id], reBuy: value },
                            }))
                          }
                          style={{ width: 100 }}
                        />
                        <IconButton
                          icon={<GoPlus />}
                          appearance="primary"
                          onClick={() => submitAmount(rowData.id, "reBuy")}
                        />
                      </div>
                    </Popover>
                  }
                >
                  <span />
                </Whisper>
              </>
            )}
          </Cell>
        </Column>
        }
      </Table>

      <Button onClick={generateGame} style={{ marginBottom: 10 }}>
        Generate
      </Button>
    </div>
  );
}
