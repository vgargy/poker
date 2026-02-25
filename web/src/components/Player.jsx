import { useEffect, useState } from "react";
import api from "../util/api";
import { Table, Button,  Tabs} from "rsuite";
const { Column, HeaderCell, Cell } = Table;
import CheckIcon from '@rsuite/icons/Check';
import CloseIcon from '@rsuite/icons/Close';

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer
} from 'recharts';

export default function Player({ playerId, onBack }) {

    const [name, setName] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [transactionData, setTransactionData] = useState([]);

    useEffect(() => {
        loadPlayer();
    }, [playerId]);

    const loadPlayer = () => {
        api.get(`/poker/player/${playerId}`)
        .then(response => {
            setName(response.name || []);
            setTransactions(response.transactions);

            setTransactionData(response.transactions.map(t => ({
                date: t.date,
                amount: t.direction === "CREDIT" ? t.amount : -t.amount
            })));

        })
    } ;

    return (
        <div style={{ padding: 30 }}>
        
        <Button onClick={onBack} style={{ marginBottom: 20 }}>
            {"<< Back to Games"}
        </Button>
        <div style={{
          display: "grid",
          gridTemplateColumns: "120px 1fr",
          gap: 8,
          marginBottom: 30
        }}>
        <strong>Name</strong>
        <div>{name}</div>
        <div style={{ width: 800}}>
        <Tabs defaultActiveKey="transactions" appearance="subtle" >
            <Tabs.Tab eventKey="transactions" title="Transactions" >
                <Table height={700} data={transactions}  style={{ padding:10 }}>

                <Column width={155}>
                    <HeaderCell>Date</HeaderCell>
                    <Cell dataKey="date" />
                </Column>
        
        
                <Column width={100}>
                    <HeaderCell>Direction</HeaderCell>
                    <Cell dataKey="direction" />
                </Column>
        
                <Column width={200}>
                    <HeaderCell>Description</HeaderCell>
                    <Cell dataKey="description" />
                </Column>
                <Column width={100}>
                    <HeaderCell>Amount</HeaderCell>
                    <Cell dataKey="amount" />
                </Column>       
                <Column width={100}>
                    <HeaderCell>Dues Cleared</HeaderCell>
                    <Cell>
                        {rowData => rowData.duesCleared ? (
                            <CheckIcon style={{ color: 'green' }} />
                        ) : (
                            <CloseIcon style={{ color: 'red' }} />
                        )}
                    </Cell>
                </Column>          
                </Table>
            </Tabs.Tab>
            <Tabs.Tab eventKey="stats" title="Statistics">

            {transactionData && <ResponsiveContainer width="60%" height={300}>
            <LineChart data={transactionData}  style={{ padding:10 }} >
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Line dataKey="amount" />
            </LineChart>
            </ResponsiveContainer>
            }

            </Tabs.Tab>
        </Tabs>
        </div>
        </div>
      </div>
    );
}