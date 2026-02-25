import { useEffect, useState } from "react";
import { Table, Button, Input, Stack, toaster, Message, Pagination, DatePicker} from "rsuite";
import Game from "./Game";
import api from "../util/api";
import Menu from "../shared/Menu";
import MenuItem from "../shared/MenuItem";
const { Column, HeaderCell, Cell } = Table;

export default function Games() {
  const [games, setGames] = useState([]);
  const [selectedGame, setSelectedGame] = useState(null);
  const [venue, setVenue] = useState("");
  const [date, setDate] = useState(null);

  useEffect(() => {
    loadGames();
  }, []);

  const goToGame = (rowData) => {
    setSelectedGame(rowData.id);
  }

  const loadGames =  () => {
    api.get('/poker/games')
    .then(response => {
        setGames(response);
    })
    .catch(err => console.error(err));
  };

    const generateGame = (gameId) => {
        api.get(`/poker/${gameId}/generate`, {
          responseType: "blob" 
        })
        .then(response => {
          const blob = response.data;
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
        });
  }
  const addGame =  () => {

    try {
        if(!venue) {
          throw new Error("Venue is required");
        }

        const dateStr = date?.toISOString().split('T')[0];

        api.post(`/poker/game?venue=${venue}&date=${dateStr}`, {
          method: "POST",
          headers: { "Content-Type": "application/json" }
        })
        .then(response => {
          setVenue("");
          setDate(null);
          loadGames();
        });

      } catch (err) {
        toaster.push(
          <Message type="error">{err.message}</Message>,
          { placement: "topEnd" }
        );
      }
  };

  if (selectedGame) {
    return (
      <Game
        gameId={selectedGame}
        onBack={() => setSelectedGame(null)} // back button in Game.jsx
      />
    );
  }

  return (
      <div style={{ paddingTop: 50, paddingRight: 20, paddingBottom: 20, paddingLeft: 20 }}>
      <Stack spacing={10} alignItems="center" style={{ marginBottom: 10 }}>
         <Input
          placeholder="Venue"
          value={venue}
          onChange={setVenue}
          style={{ width: 200 }}
        />
        <DatePicker 
          format="MM/dd/yyyy"
          placeholder="Date"
          value={date}
          onChange={setDate}/>
        <Button appearance="primary" onClick={addGame} alignItems="right">
          New
        </Button>
      </Stack>

      <Table height={700} data={games} >

        <Column flexGrow={1}>
          <HeaderCell>Date</HeaderCell>
          <Cell>
            {rowData => (
              <div onClick={() => goToGame(rowData)} style={{ cursor: "pointer" }}>
                {rowData.date}
              </div>
            )}
          </Cell>
        </Column>

        <Column flexGrow={1}>
          <HeaderCell>Venue</HeaderCell>
          <Cell dataKey="venue" />
        </Column>

        <Column flexGrow={1}>
          <HeaderCell>Status</HeaderCell>
          <Cell dataKey="status" />
        </Column>
        <Column flexGrow={1}>
          <HeaderCell></HeaderCell>
             <Cell>
              {rowData => (
             <Stack justifyContent="space-between">
              <div></div>
              <Menu menuItems={[
                <MenuItem name="edit" label={"Generate"} 
                  action={() => generateGame(rowData.id)} 
                  disabled = {rowData.status != 'Settled'}/>,
              ]}/>
             </Stack>
              )}
             </Cell>
        </Column>
      </Table>
      <Pagination limit={1}/>

    </div>
  );
}
