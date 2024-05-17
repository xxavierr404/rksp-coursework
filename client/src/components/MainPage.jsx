import React, {useEffect} from 'react';
import {Button, Card, CardBody, Col, Container, Row} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.css';
import LogoLink from "./LogoLink";
import {Link} from "react-router-dom";
import Cookies from "js-cookie";

const MainPage = () => {
    useEffect(() => {
        if (Cookies.get("workus-tkn")) {
            window.location = "/profile";
        }
    });

    return (
        <Container fluid className={"vh-100 bg-dark bg-gradient row-gap-3"}>
            <Row className={"py-3 justify-content-between mx-xxl-5"}>
                <LogoLink/>
            </Row>
            <Row className={"vh-50 pt-3 mx-xxl-5 align-items-stretch"}>
                <Col md={6}>
                    <Card className={"bg-dark"}>
                        <CardBody>
                            <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                                Вы - руководитель организации?
                            </Row>
                            <Row className={"mt-3"}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" fill="white"
                                     className="bi bi-building" viewBox="0 0 16 16">
                                    <path fillRule="evenodd"
                                          d="M14.763.075A.5.5 0 0 1 15 .5v15a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5V14h-1v1.5a.5.5 0 0 1-.5.5h-9a.5.5 0 0 1-.5-.5V10a.5.5 0 0 1 .342-.474L6 7.64V4.5a.5.5 0 0 1 .276-.447l8-4a.5.5 0 0 1 .487.022zM6 8.694 1 10.36V15h5V8.694zM7 15h2v-1.5a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 .5.5V15h2V1.309l-7 3.5V15z"/>
                                    <path
                                        d="M2 11h1v1H2v-1zm2 0h1v1H4v-1zm-2 2h1v1H2v-1zm2 0h1v1H4v-1zm4-4h1v1H8V9zm2 0h1v1h-1V9zm-2 2h1v1H8v-1zm2 0h1v1h-1v-1zm2-2h1v1h-1V9zm0 2h1v1h-1v-1zM8 7h1v1H8V7zm2 0h1v1h-1V7zm2 0h1v1h-1V7zM8 5h1v1H8V5zm2 0h1v1h-1V5zm2 0h1v1h-1V5zm0-2h1v1h-1V3z"/>
                                </svg>
                            </Row>
                            <Row className={"mt-3 fs-4 text-white text-lg-center justify-content-center"}>
                                Зарегистрируйте свою организацию и пригласите сотрудников - это бесплатно!
                            </Row>
                            <Row className={"mt-3 fs-4 text-white text-lg-center justify-content-center"}>
                                Уже зарегистрированы? Нужно всего лишь войти по логину и паролю.
                            </Row>
                            <Row className={"mt-3 mx-2"}>
                                <Link to={"/register"}>
                                    <Button variant={"success"} className={"w-100"}>
                                        Зарегистрироваться
                                    </Button>
                                </Link>
                            </Row>
                            <Row className={"mt-3 mx-2"}>
                                <Link to={"/login"}>
                                    <Button variant={"info"} className={"w-100"}>
                                        Войти
                                    </Button>
                                </Link>
                            </Row>
                        </CardBody>
                    </Card>
                </Col>
                <Col md={6}>
                    <Card className={"bg-dark"}>
                        <CardBody>
                            <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                                Вы - сотрудник организации?
                            </Row>
                            <Row className={"mt-3"}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" fill="white"
                                     className="bi bi-person-badge" viewBox="0 0 16 16">
                                    <path
                                        d="M6.5 2a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1h-3zM11 8a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
                                    <path
                                        d="M4.5 0A2.5 2.5 0 0 0 2 2.5V14a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V2.5A2.5 2.5 0 0 0 11.5 0h-7zM3 2.5A1.5 1.5 0 0 1 4.5 1h7A1.5 1.5 0 0 1 13 2.5v10.795a4.2 4.2 0 0 0-.776-.492C11.392 12.387 10.063 12 8 12s-3.392.387-4.224.803a4.2 4.2 0 0 0-.776.492V2.5z"/>
                                </svg>
                            </Row>
                            <Row className={"mt-3 fs-4 text-white text-lg-center justify-content-center"}>
                                Запросите у руководителя данные для входа в аккаунт.
                            </Row>
                            <Row className={"mt-3 text-white justify-content-center"}>
                                <Link to={"/login"}>
                                    <Button variant={"info"} className={"w-100"}>
                                        Войти
                                    </Button>
                                </Link>
                            </Row>
                        </CardBody>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default MainPage;