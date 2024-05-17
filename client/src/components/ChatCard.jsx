import React from 'react';
import {Button, Card, Col, Row} from "react-bootstrap";
import axios from "axios";
import Cookies from "js-cookie";

const ChatCard = (props) => {
    const join = async () => {
        axios.post(
            `http://${process.env.REACT_APP_SERVER_IP}:5553/api/v1/chat/${props.chatId}/${props.userId}?isManager=false`,
            {},
            {
                headers: {
                    Authorization: `Bearer ${Cookies.get("workus-tkn")}`
                }
            }
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    return null;
                }
                return resp;
            })
            .then(resp => {
                if (resp !== null) {
                    window.location = `/chat/${props.chatId}`;
                }
            })
    };

    return (
        <Row sm={2} className={"justify-content-center my-2"}>
            <Card className={"bg-light"}>
                <Row className={"text-lg-center fs-3 text-black justify-content-center"}>
                    <Col>
                        {props.name}
                    </Col>
                    <Col>
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill={props.isPublic ? "green" : "red"}
                             className="bi bi-door-open" viewBox="0 0 16 16">
                            <path d="M8.5 10c-.276 0-.5-.448-.5-1s.224-1 .5-1 .5.448.5 1-.224 1-.5 1z"/>
                            <path
                                d="M10.828.122A.5.5 0 0 1 11 .5V1h.5A1.5 1.5 0 0 1 13 2.5V15h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H3V1.5a.5.5 0 0 1 .43-.495l7-1a.5.5 0 0 1 .398.117zM11.5 2H11v13h1V2.5a.5.5 0 0 0-.5-.5zM4 1.934V15h6V1.077l-6 .857z"/>
                        </svg>
                    </Col>
                </Row>
                <Row className={"text-lg-center fs-5 text-black justify-content-center"}>
                    {props.description}
                </Row>
                {
                    props.isJoined
                        ? <Button variant={"success"} className={"w-100 my-md-3"} onClick={() => window.location = `/chat/${props.chatId}`}>
                            Открыть
                        </Button>
                        : <Button variant={"primary"} className={"w-100 my-md-3"} onClick={() => join()}>
                            Присоединиться
                        </Button>
                }
            </Card>
        </Row>
    );
};

export default ChatCard;