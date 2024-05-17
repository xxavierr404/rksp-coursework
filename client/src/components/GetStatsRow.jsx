import React, {useEffect, useState} from 'react';
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import axios from "axios";
import Cookies from "js-cookie";
import RemoveMemberRow from "./RemoveMemberRow";

const AddMemberRow = (props) => {
    const [profile, setProfile] = useState("");
    const [chatTimeStats, setChatTimeStats] = useState({});
    const [messageStats, setMessageStats] = useState({});
    const [statDialogActive, setStatDialogActive] = useState(false);

    useEffect(() => {
        updateProfile();
    }, []);

    const updateProfile = async () => {
        await axios.get(
            `http://${process.env.REACT_APP_SERVER_IP}:5552/api/v1/user-profile/${props.id}`,
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
            .then(resp => setProfile(resp.data));
    }

    const updateStatInfo = async () => {
        await axios.get(
            `http://${process.env.REACT_APP_SERVER_IP}:5554/api/v1/chat-time?chatId=${props.chatId}&userId=${props.id}`,
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
                if (resp) {
                    setChatTimeStats(resp.data);
                }
            });
        await axios.get(
            `http://${process.env.REACT_APP_SERVER_IP}:5554/api/v1/messages?chatId=${props.chatId}&userId=${props.id}`,
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
                if (resp) {
                    setMessageStats(resp.data);
                }
            });
    }

    return (
        <Form.Group className={"mt-3 w-100"}>
            <Row className={"my-3"}>
                <Col className={"text-md-center"}>
                    {profile.firstName} {profile.lastName}
                </Col>
                <Col sm={4}>
                    <Button variant={"success"} className={"h-100 w-100"}
                            onClick={() => {
                                updateStatInfo();
                                setStatDialogActive(true);
                            }}>
                        Посмотреть
                    </Button>
                </Col>
            </Row>
            <Modal show={statDialogActive}>
                <Modal.Header closeButton onHide={() => setStatDialogActive(false)}>
                    <Modal.Title>Статистика {profile.firstName} {profile.lastName}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Row>
                        <Col>
                            Среднее время присутствия в сети:
                        </Col>
                        <Col sm={4}>
                            {parseInt(chatTimeStats.meanPresenceTimePerVisitMillis) / 60000} минут
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            Самый активный день (по времени в сети):
                        </Col>
                        <Col sm={3}>
                            {chatTimeStats.mostActiveDay}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            Всего отправлено сообщений:
                        </Col>
                        <Col sm={3}>
                            {messageStats.totalMessageCount}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            Среднее количество сообщений в день:
                        </Col>
                        <Col sm={3}>
                            {messageStats.meanMessagesCountPerDay}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            Самый активный день (по кол-ву сообщений):
                        </Col>
                        <Col sm={3}>
                            {messageStats.mostActiveDay}
                        </Col>
                    </Row>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setStatDialogActive(false)}>Закрыть</Button>
                </Modal.Footer>
            </Modal>
        </Form.Group>
    );
};

export default AddMemberRow;