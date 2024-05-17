import React, {useEffect, useState} from 'react';
import {Button, Card, Col, Container, Form, Modal, Row} from "react-bootstrap";
import LogoLink from "./LogoLink";
import Cookies from "js-cookie";
import axios from "axios";
import {Link} from "react-router-dom";
import ChatCard from "./ChatCard";

const ChatList = (props) => {
    const [chats, setChats] = useState([]);
    const [potentialChats, setPotentialChats] = useState([]);

    const [newChatModalActive, setChatModalActive] = useState(false);
    const [newChatName, setNewChatName] = useState("");
    const [newChatDescription, setNewChatDescription] = useState("");
    const [newChatPublicity, setNewChatPublicity]  = useState(false);

    const [userId, setUserId] = useState();

    useEffect(() => {
        if (!Cookies.get("workus-tkn")) {
            window.location = "/";
        }
    });

    useEffect(() => {
        updateUserId();
        updateChatList();
        updatePotentialChatList();
    }, []);

    const updateChatList = async () => {
        axios.get(
            "http://localhost:5553/api/v1/chats",
            {
                headers: {
                    Authorization: `Bearer ${Cookies.get("workus-tkn")}`
                }
            }
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    console.log("Something bad happened");
                    return null;
                }
                return resp;
            })
            .then(resp => {
                if (resp !== null) {
                    setChats(resp.data);
                }
            })
    };

    const updatePotentialChatList = async () => {
        axios.get(
            "http://localhost:5553/api/v1/chats/available",
            {
                headers: {
                    Authorization: `Bearer ${Cookies.get("workus-tkn")}`
                }
            }
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    console.log("Something bad happened");
                    return null;
                }
                return resp;
            })
            .then(resp => {
                if (resp !== null) {
                    setPotentialChats(resp.data);
                }
            })
    };

    const createChat = async () => {
        axios.post(
            "http://localhost:5553/api/v1/chat",
            {
                name: newChatName,
                description: newChatDescription,
                isPublic: newChatPublicity
            },
            {
                headers: {
                    Authorization: `Bearer ${Cookies.get("workus-tkn")}`
                }
            }
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    console.log("Something bad happened");
                    return null;
                }
                return resp;
            })
            .then(resp => {
                if (resp !== null) {
                    updateChatList();
                    setChatModalActive(false);
                }
            })
    }

    const updateUserId = () => {
        return axios.get(
            'http://localhost:5552/api/v1/user-profile',
            {
                headers: {
                    Authorization: `Bearer ${Cookies.get("workus-tkn")}`
                }
            }
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    console.log("Something bad happened");
                    return null;
                }
                return resp;
            })
            .then(resp => setUserId(resp.data.id));
    }

    const logout = async () => {
        Cookies.remove("workus-tkn");
        window.location = "/";
    }

    return (
        <div>
            <Container fluid className={"vh-100 bg-dark bg-gradient row-gap"}>
                <Row className={"py-3 justify-content-between mx-xxl-5"}>
                    <Col md={4}>
                        <LogoLink></LogoLink>
                    </Col>
                    <Col md={2}>
                        <Button variant={"danger"} className={"w-100"} onClick={() => logout()}>
                            Выйти
                        </Button>
                    </Col>
                </Row>
                <Row md={2} className={"justify-content-center"}>
                    <Card className={"bg-dark justify-content-center"}>
                        <Button variant={"success"} className={"w-100 my-2"} onClick={() => setChatModalActive(true)}>
                            Создать чат
                        </Button>
                        <Modal show={newChatModalActive}>
                            <Modal.Header closeButton onHide={() => setChatModalActive(false)}>
                                <Modal.Title>Создание чата</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                <Form.Group className={"mt-3"}>
                                    <Form.Label>Название чата</Form.Label>
                                    <Form.Control
                                        required
                                        size={"lg"}
                                        type={"text"}
                                        placeholder={"Введите название..."}
                                        onChange={event => setNewChatName(event.target.value)}
                                    ></Form.Control>
                                </Form.Group>
                                <Form.Group className={"mt-3"}>
                                    <Form.Label>Описание чата</Form.Label>
                                    <Form.Control
                                        required
                                        size={"lg"}
                                        type={"text"}
                                        placeholder={"Введите описание..."}
                                        onChange={event => setNewChatDescription(event.target.value)}
                                    ></Form.Control>
                                </Form.Group>
                                <Form.Group className={"mt-3"}>
                                    <Form.Label>Публичный ли чат?</Form.Label>
                                    <Form.Check
                                        required
                                        type={"checkbox"}
                                        onChange={event => setNewChatPublicity(event.target.checked)}
                                    ></Form.Check>
                                </Form.Group>
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => setChatModalActive(false)}>Отменить</Button>
                                <Button variant="primary" onClick={() => createChat()}>Создать</Button>
                            </Modal.Footer>
                        </Modal>
                        <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                            Список чатов
                        </Row>
                        {
                            chats.length > 0
                            ? chats.map(
                                chat => <ChatCard
                                    name={chat.name}
                                    description={chat.description}
                                    isPublic={chat.isPublic}
                                    chatId={chat.id}
                                    isJoined={true}
                                ></ChatCard>
                            )
                                : <Row className={"text-lg-center fs-4 text-white justify-content-center"}>
                                    Похоже, пока что Вы не состоите ни в одном чате...
                                </Row>
                        }
                    </Card>
                </Row>
                <Row md={2} className={"justify-content-center mt-3"}>
                    <Card className={"bg-dark"}>
                        <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                            Список публичных чатов вашей организации
                        </Row>
                        {
                            potentialChats.length > 0
                            ? potentialChats.map(
                                chat => <ChatCard
                                    name={chat.name}
                                    description={chat.description}
                                    isPublic={chat.isPublic}
                                    chatId={chat.id}
                                    userId={userId}
                                    isJoined={false}
                                ></ChatCard>
                            )
                                : <Row className={"text-lg-center fs-4 text-white justify-content-center"}>
                                    У Вашей организации пока нет публичных чатов...
                                </Row>
                        }
                    </Card>
                </Row>
            </Container>
        </div>
    );
};

export default ChatList;