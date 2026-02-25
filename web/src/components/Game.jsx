import { useEffect, useState } from "react";
import {
  Table,
  Button,
  Input,
  IconButton,
  Whisper,
  Popover,
  Stack
} from "rsuite";

import CreatableSelect from "react-select/creatable";
import { PiPokerChipThin } from "react-icons/pi";
import { GoPlus } from "react-icons/go";
import Menu from "../shared/Menu";
import MenuItem from "../shared/MenuItem";
import api from "../util/api";
import Player from "./Player"


const { Column, HeaderCell, Cell } = Table;

export default function Game({ gameId, onBack }) {
  const [players, setPlayers] = useState([]);
  const [data, setData] = useState(null);
  const [allPlayers, setAllPlayers] = useState([]);
  const [activeRowId, setActiveRowId] = useState(null);
  const [amount, setAmount] = useState({});
  const [selectedPlayer, setSelectedPlayer] = useState(null);

  useEffect(() => {
    loadGame();
  }, [gameId]);

  useEffect(() => {
    listPlayers();
  }, []);


  const settleGame = () => {
    api.post(`/poker/${gameId}/settle`)
    .then(response => {
      onBack();
    })

  }

  const loadGame = () => {
    api.get(`/poker/game/${gameId}`)
    .then(response => {
        setPlayers(response.players || []);
        setData(response);
    })
  };

  const listPlayers =  () => {
    api.get("/poker/players")
    .then(response => {
      setAllPlayers(
        response.map(p => ({
          label: p.name,
          value: p.id
        }))
      );
    });
  };

  const addPlayer = (playerInfo) => {
    api.post(`/poker/${gameId}/player/${playerInfo.value}/add`)
    .then(response => {
      setPlayers(response.players || []);
    })
  }

  const addNewPlayer = (fullName) => {
    const name = fullName.split(" ");
    const firstName = name[0];
    const lastName = name.length > 1 ? name[1] : name[0];

    api.post(`/poker/${gameId}/player/${firstName}/${lastName}/add`)
    .then(response => {
      setPlayers(response.players || []);
    })
  }

  const renderTotalAmount = (rowData) =>
    (rowData.cashIn+rowData.credit) - rowData.buyIns;

  const submitAmount = async (playerId, action) => {
    const pAmount = amount !== null ? amount[playerId]?.[action] : null;
    if(pAmount === null || pAmount === undefined) {
      return;
    }
    api.post(`/poker/${gameId}/player/${playerId}/${action}/${pAmount}`)
    .then(response => {
      setActiveRowId(null);
      setAmount({});
      loadGame();
    });
  };

  const goToPlayer = (rowData) => {
    setSelectedPlayer(rowData.id);
  }

  if (selectedPlayer) {
    return (
      <Player
        playerId={selectedPlayer}
        onBack={() => setSelectedPlayer(null)} // back button in Game.jsx
      />
    );
  }


  return (
    <div style={{ padding: 20 }}>
      <Button onClick={onBack} style={{ marginBottom: 10 }}>
        {"<< Back to Games"}
      </Button>
       
        <Stack justifyContent="space-between">
          <div></div>
          {data && (
            <Menu menuItems={[
              <MenuItem name="settle" label={"Settle"} 
              action={settleGame}
              disabled={data?.status === 'Settled'}/>
            ]}/>
          )}
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
        onChange={addPlayer}
        onCreateOption={addNewPlayer}
        styles={{
          container: base => ({ ...base, width: 300 }),
          menuPortal: base => ({ ...base, zIndex: 9999 })
        }}
      />

      <Table height={1000} data={players}>
        <Column width={155}>
          <HeaderCell>Name</HeaderCell>
          <Cell>
            {rowData => (
              <div onClick={() => goToPlayer(rowData)} style={{ cursor: "pointer" }}>
                {rowData.name}
              </div>
            )}
          </Cell>
        </Column>

        <Column width={100} align="center">
          <HeaderCell>Buy Ins</HeaderCell>
          <Cell dataKey="buyIns" />
        </Column>

        <>
        <Column width={110} >
          <HeaderCell>Credit</HeaderCell>
          <Cell>
             {(rowData) => (
              <>
                <Input
                  disabled = {data?.status === 'Settled'}
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
                  disabled = {data?.status === 'Settled'}
                  type="number"
                  step={5}
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
      
        {data?.status !== 'Settled' &&
        <Column width={60} align="center" fixed="right">
          <HeaderCell />
            <Cell>
              {(rowData) => (
                <Whisper
                  trigger="manual"
                  placement="left"
                  container={() => document.body}
                  open={activeRowId === rowData.id}
                  rootClose
                  onClose={() => {
                    setActiveRowId(null);
                    setAmount({});
                  }}
                  speaker={
                    <Popover style={{ padding: 10 }}>
                      <div style={{ display: "flex", gap: 8 }}>
                        <div>{rowData.name}</div>
                        <Input
                          type="number"
                          value={amount[rowData.id]?.reBuy || ""}
                          onChange={(value) =>
                            setAmount((prev) => ({
                              ...prev,
                              [rowData.id]: { ...prev[rowData.id], reBuy: value },
                            }))
                          }
                        />
                        <IconButton
                          appearance="primary"
                          icon={<GoPlus />}
                          onClick={() => submitAmount(rowData.id, "reBuy")}
                        />
                      </div>
                    </Popover>
                  }
                >
                  <div style={{ display: "inline-block" }}>
                  <PiPokerChipThin style={{ cursor: 'pointer' }} onClick={() => setActiveRowId(rowData.id)}/>  
                  </div>
                </Whisper>
              )}
            </Cell>

        </Column>
        }
      </Table>
    </div>
  );
}
