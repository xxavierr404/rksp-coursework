import React, {useEffect, useState} from 'react';
import {Stomp} from "@stomp/stompjs";

const TestWs = () => {
    const [messages, setMessages] = useState([]);

    useEffect(() => {
        const test = async () => {
            let socket = new WebSocket(`ws://${process.env.SERVER_IP}:5555/api/v1/messaging`);
            let token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYWFhIiwiZXhwIjoxNzEzMDQzODMzLCJpZCI6MSwicm9sZSI6Ik9SR0FOSVpBVElPTiJ9.-BWmORY7MokKnsh5ieoBpOBHdAkGJljfpUzD9WItSGS7NjvoX-z0RtQFyeoye_Q4I442WNDDqvAf4_mh_JDkLA";
            let authHeader = {
                "X-Authorization": token
            };
            let client = Stomp.over(socket);
            client.configure(
                {
                    onStompError: err => console.log(err),
                    onWebSocketError: err => console.log(err)
                }
            );
            client.connect(
                authHeader,
                () => {
                    client.subscribe(
                        "/topic/chat/1",
                        msg => setMessages(JSON.parse(msg.body)),
                        authHeader
                    );
                    client.send(
                        "/app/send-message",
                        authHeader,
                        JSON.stringify({
                            chatId: 1,
                            text: "ababab"
                        })
                    );
                }
            );
        };

        test();
    }, []);

    return (
        <div>
            {
                JSON.stringify(messages)
            }
        </div>
    );
};

export default TestWs;