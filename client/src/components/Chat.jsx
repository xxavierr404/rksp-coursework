import React, {useCallback, useEffect, useState} from 'react';
import {Button, Card, Col, Container, Form, Modal, Row} from "react-bootstrap";
import LogoLink from "./LogoLink";
import ChatMessage from "./ChatMessage";
import Cookies from "js-cookie";
import {Stomp} from "@stomp/stompjs";
import {useParams} from "react-router-dom";
import axios from "axios";
import {useStompClient, useSubscription} from "react-stomp-hooks";
import AddMemberRow from "./AddMemberRow";
import RemoveMemberRow from "./RemoveMemberRow";
import GetStatsRow from "./GetStatsRow";

const Chat = () => {
    const [chatInfo, setChatInfo] = useState({});
    const [messages, setMessages] = useState([]);
    const [newMessageText, setNewMessageText] = useState("");
    const [userId, setUserId] = useState(0);
    const [isManager, setIsManager] = useState(false);
    const [addMemberModalActive, setAddMemberModalActive] = useState(false);
    const [removeMemberModalActive, setRemoveMemberModalActive] = useState(false);
    const [potentialMembers, setPotentialMembers] = useState([]);
    const [statisticsModalActive, setStatisticsModalActive] = useState(false);

    const authHeader = {
        "X-Authorization": `Bearer ${Cookies.get("workus-tkn")}`
    };

    let {id} = useParams();
    const client = useStompClient();
    useSubscription(
        `/topic/chat/${id}`,
        msg => setMessages([...messages, JSON.parse(msg.body)]),
        authHeader);

    useEffect(() => {
        if (!Cookies.get("workus-tkn")) {
            window.location = "/";
        }
    }, []);

    useEffect(() => {
        getChatInfo();
        updatePotentialMembers();
    }, []);

    useEffect(() => {
        updateManagerRights();
    }, [userId]);

    const sendMessage = async () => {
        await client.publish(
            {
                destination: "/app/send-message",
                headers: authHeader,
                body: JSON.stringify({
                    chatId: id,
                    text: newMessageText
                })
            }
        );
        setNewMessageText("");
    }

    const updateManagerRights = async () => {
        await axios.get(
            `http://localhost:5553/api/v1/chat/${id}/${userId}/check-rights`,
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
                    setIsManager(resp.data);
                } else {
                    window.location = "/chats";
                }
            });
    }

    const getChatInfo = async () => {
        await axios.get(
            `http://localhost:5552/api/v1/user-profile`,
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
                setUserId(resp.data.id);
            });
        await axios.get(
            `http://localhost:5553/api/v1/chat/${id}`,
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
                    setChatInfo(resp.data);
                } else {
                    window.location = "/chats";
                }
            });
        await axios.get(
            `http://localhost:5555/api/v1/messages/history/${id}`,
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
                    setMessages(resp.data);
                } else {
                    window.location = "/chats";
                }
            });
    };

    const updatePotentialMembers = async () => {
        axios.get(
            `http://localhost:5553/api/v1/chat/${id}/potential-members`,
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
                    setPotentialMembers(resp.data);
                }
            });
    };

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
                <Row md={2} className={"justify-content-center h-75"}>
                    <Card className={"bg-dark justify-content-between h-100"}>
                        <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                            Чат {chatInfo.name}
                        </Row>
                        <Container className={"h-75 justify-content-end flex-column overflow-y-scroll"}>
                            {
                                messages.length > 0
                                    ? messages.map(it =>
                                        <ChatMessage key={`${it.authorId}/${id}/${it.sendingTime}`} author={it.authorId}
                                                     text={it.text} time={it.sendingTime}>

                                        </ChatMessage>
                                    )
                                    : <Row className={"text-lg-center fs-4 text-white justify-content-center"}>
                                        Здесь пусто... Отправьте первое сообщение!
                                    </Row>
                            }
                        </Container>
                        <Form.Group className={"mt-3 text-white w-100"}>
                            <Row className={"my-3"}>
                                <Col>
                                    <Form.Control
                                        required
                                        size={"lg"}
                                        type={"text"}
                                        placeholder={"Начните писать сообщение..."}
                                        value={newMessageText}
                                        onChange={event => setNewMessageText(event.target.value)}
                                    ></Form.Control>
                                </Col>
                                <Col sm={2}>
                                    <Button variant={"success"} className={"h-100 w-100"}
                                            onClick={() => sendMessage()}>
                                        Отправить
                                    </Button>
                                </Col>
                            </Row>
                        </Form.Group>
                        {
                            isManager && <Row>
                                <Button variant={"success"} className={"h-100 w-100"}
                                        onClick={() => setAddMemberModalActive(true)}>
                                    Добавить участника
                                </Button>
                            </Row>
                        }
                        {
                            isManager && <Row>
                                <Button variant={"secondary"} className={"h-100 w-100"}
                                        onClick={() => setRemoveMemberModalActive(true)}>
                                    Выгнать участника
                                </Button>
                            </Row>
                        }
                        {
                            isManager && <Row>
                                <Button variant={"primary"} className={"h-100 w-100"}
                                        onClick={() => setStatisticsModalActive(true)}>
                                    Просмотреть статистику
                                </Button>
                            </Row>
                        }
                    </Card>
                </Row>
            </Container>
            <Modal show={addMemberModalActive}>
                <Modal.Header closeButton onHide={() => setAddMemberModalActive(false)}>
                    <Modal.Title>Добавление участника в чат</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                        potentialMembers.length > 0
                            ? potentialMembers.map(it => <AddMemberRow id={it.id} chatId={id} updatePotentialMembers={updatePotentialMembers}></AddMemberRow>)
                            : <Row className={"text-lg-center fs-4 justify-content-center"}>
                                Пригласить пока что некого.
                            </Row>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setAddMemberModalActive(false)}>Закрыть</Button>
                </Modal.Footer>
            </Modal>
            <Modal show={removeMemberModalActive}>
                <Modal.Header closeButton onHide={() => setRemoveMemberModalActive(false)}>
                    <Modal.Title>Удаление участника из чата</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                        chatInfo.members && chatInfo.members.length > 0
                            ? chatInfo.members.map(it => <RemoveMemberRow id={it.id} chatId={id} updateChatInfo={getChatInfo}></RemoveMemberRow>)
                            : <Row className={"text-lg-center fs-4 justify-content-center"}>
                                Здесь никого нет..? Что-то явно пошло не так.
                            </Row>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setRemoveMemberModalActive(false)}>Закрыть</Button>
                </Modal.Footer>
            </Modal>
            <Modal show={statisticsModalActive}>
                <Modal.Header closeButton onHide={() => setStatisticsModalActive(false)}>
                    <Modal.Title>Просмотр статистики участников</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                        chatInfo.members && chatInfo.members.length > 0
                            ? chatInfo.members.map(it => <GetStatsRow id={it.id} chatId={id}></GetStatsRow>)
                            : <Row className={"text-lg-center fs-4 justify-content-center"}>
                                Здесь никого нет..? Что-то явно пошло не так.
                            </Row>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setStatisticsModalActive(false)}>Закрыть</Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default Chat;