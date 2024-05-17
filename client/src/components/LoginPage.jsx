import React, {useEffect, useState} from 'react';
import {Button, Card, Container, Form, Row} from "react-bootstrap";
import LogoLink from "./LogoLink";
import Cookies from "js-cookie";
import axios from "axios";

const LoginPage = (props) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    useEffect(() => {
        if (Cookies.get("workus-tkn")) {
            window.location = "/";
        }
    });

    const login = async () => {
        let loginRequest = {
            login: username,
            password: password
        };

        axios.post(
            `http://${process.env.REACT_APP_SERVER_IP}:5551/api/v1/login`,
            loginRequest
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    return null;
                }
                return resp;
            })
            .then(resp => {
                if (resp !== null) {
                    Cookies.set("workus-tkn", resp.data.accessToken, { expires: 1 });
                    window.location = "/";
                }
            })
    };

    return (
        <div>
            <Container fluid className={"vh-100 bg-dark bg-gradient row-gap-3"}>
                <Row className={"py-3 justify-content-between mx-xxl-5"}>
                    <LogoLink></LogoLink>
                </Row>
                <Row md={2} className={"justify-content-center"}>
                    <Card className={"bg-dark"}>
                        <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                            Вход
                        </Row>
                        <Row className={"text-lg-center fs-5 text-white justify-content-center"}>
                            Если Вы - сотрудник, данные для входа Вам должна предоставить Ваша организация.
                        </Row>
                        <Form className={"bg-dark px-2"}>
                            <Form.Group className={"mt-3 text-white"}>
                                <Form.Label>Логин</Form.Label>
                                <Form.Control
                                    required
                                    size={"lg"}
                                    type={"text"}
                                    placeholder={"Введите логин..."}
                                    onChange={event => setUsername(event.target.value)}
                                ></Form.Control>
                            </Form.Group>
                            <Form.Group className={"mt-3 text-white"}>
                                <Form.Label>Пароль</Form.Label>
                                <Form.Control
                                    required
                                    size={"lg"}
                                    type={"password"}
                                    placeholder={"Введите пароль..."}
                                    onChange={event => setPassword(event.target.value)}
                                ></Form.Control>
                            </Form.Group>
                            <Row className={"my-3 px-2"}>
                                <Button variant={"info"} className={"w-100"} onClick={() => login()}>
                                    Войти
                                </Button>
                            </Row>
                        </Form>
                    </Card>
                </Row>
            </Container>
        </div>
    );
};

export default LoginPage;