import React, {useEffect, useState} from 'react';
import {Button, Card, Container, Form, Row} from "react-bootstrap";
import LogoLink from "./LogoLink";
import axios from "axios";
import Cookies from "js-cookie";

const RegisterPage = (props) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [organization, setOrganization] = useState("");

    useEffect(() => {
        if (Cookies.get("workus-tkn")) {
            window.location = "/";
        }
    });

    const register = async () => {
        let registerRequest = {
            login: username,
            password: password,
            firstName: organization,
            role: "ORGANIZATION"
        };

        axios.post(
            `http://${process.env.REACT_APP_SERVER_IP}:5551/api/v1/register`,
            registerRequest
        )
            .catch(resp => {
                if (resp.status !== 200) {
                    return null;
                }
                return resp;
            })
            .then(resp => {
                if (resp !== null) {
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
                            Регистрация
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
                            <Form.Group className={"mt-3 text-white"}>
                                <Form.Label>Название организации</Form.Label>
                                <Form.Control
                                    required
                                    size={"lg"}
                                    type={"text"}
                                    placeholder={"Например, Xavi LTD."}
                                    onChange={event => setOrganization(event.target.value)}
                                ></Form.Control>
                            </Form.Group>
                            <Row className={"my-3 px-2"}>
                                <Button variant={"success"} className={"w-100"} onClick={() => register()}>
                                    Регистрация
                                </Button>
                            </Row>
                        </Form>
                    </Card>
                </Row>
            </Container>
        </div>
    );
};

export default RegisterPage;