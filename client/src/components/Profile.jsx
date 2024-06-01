import React, {useEffect, useState} from 'react';
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import LogoLink from "./LogoLink";
import Cookies from "js-cookie";
import axios from "axios";
import {useParams} from "react-router-dom";

const Profile = () => {
    const [profile, setProfile] = useState({});
    const [organizationProfile, setOrganizationProfile] = useState({});
    const [newDescription, setNewDescription] = useState("");
    const [newPosition, setNewPosition] = useState("");

    let {id} = useParams();

    useEffect(() => {
        if (!Cookies.get("workus-tkn")) {
            window.location = "/";
        }
    });

    useEffect(() => {
        loadProfileInfo();
    }, []);

    const loadProfileInfo = async () => {
        let profileData = await getProfileInfo(id);
        setProfile(profileData);
        if (!profileData.organizationId) return;
        let organizationProfile = await getProfileInfo(profileData.organizationId);
        setOrganizationProfile(organizationProfile);
    };

    const getProfileInfo = (id) => {
        let url = id
            ? `http://${process.env.REACT_APP_SERVER_IP}:5552/api/v1/user-profile/${id}`
            : `http://${process.env.REACT_APP_SERVER_IP}:5552/api/v1/user-profile`
        return axios.get(
            url,
            {
                headers: {
                    Authorization: `Bearer ${Cookies.get("workus-tkn")}`
                }
            }
        )
            .catch(async resp => {
                if (resp.status !== 200) {
                    console.log("Something bad happened");
                    if (!id) {
                        await logout();
                    }
                    return null;
                }
                return resp;
            })
            .then(resp => resp.data);
    }

    const updateProfileInfo = async () => {
        await axios.put(
            `http://${process.env.REACT_APP_SERVER_IP}:5552/api/v1/user-profile`,
            {
                description: newDescription,
                position: newPosition
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
            .then(() => loadProfileInfo());
    }

    const logout = async () => {
        Cookies.remove("workus-tkn");
        window.location = "/";
    }

    const getProfileRole = () => {
        if (profile.role === "EMPLOYEE") {
            return "сотрудника";
        } else if (profile.role === "ORGANIZATION") {
            return "организации";
        }
    }

    return (
        <div>
            <Container fluid className={"vh-100 bg-dark bg-gradient row-gap-3"}>
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
                    <Card className={"bg-dark"}>
                        <Row className={"text-lg-center fs-3 text-white justify-content-center"}>
                            Профиль {getProfileRole()} {profile.firstName} {profile.lastName ? profile.lastName : ""}
                        </Row>
                        <Row className={"text-lg-center fs-5 text-white justify-content-center"}>
                            Id: {profile.id}
                        </Row>
                        {
                            profile.role === "EMPLOYEE" && <Row className={"text-lg-center fs-5 text-white justify-content-center"}>
                                Должность: {profile.position}
                            </Row>
                        }
                        {
                            !id && profile.role === "EMPLOYEE" && <Form.Group className={"mt-3 text-white"}>
                                <Form.Label>Обновить должность</Form.Label>
                                <Form.Control
                                    size={"lg"}
                                    type={"text"}
                                    placeholder={"Введите новую должность..."}
                                    defaultValue={profile.position}
                                    onChange={event => setNewPosition(event.target.value)}
                                ></Form.Control>
                            </Form.Group>
                        }
                        {
                            profile.organizationId && <Row className={"text-lg-center fs-5 text-white justify-content-center"}>
                                Организация: {organizationProfile.firstName}
                            </Row>
                        }
                        <Row className={"text-lg-center fs-5 text-white justify-content-center"}>
                            Описание: {profile.description ? profile.description: "Здесь пока пусто..."}
                        </Row>
                        {
                            !id && <Form.Group className={"mt-3 text-white"}>
                                <Form.Label>Обновить описание</Form.Label>
                                <Form.Control
                                    size={"lg"}
                                    type={"text"}
                                    placeholder={"Введите новое описание..."}
                                    defaultValue={profile.description}
                                    onChange={event => setNewDescription(event.target.value)}
                                ></Form.Control>
                            </Form.Group>
                        }
                        {
                            !id && <Row>
                                <Button variant={"danger"} className={"w-100 mt-md-3"} onClick={() => updateProfileInfo()}>
                                    Обновить профиль
                                </Button>
                            </Row>
                        }
                        {
                            !id && <Row>
                                <Button variant={"primary"} className={"w-100 mt-md-3"} onClick={() => window.location = "/chats"}>
                                    Чаты
                                </Button>
                            </Row>
                        }
                        {
                            !id && profile.role === "ORGANIZATION" && <Row>
                                <Button variant={"secondary"} className={"w-100 mt-md-3"} onClick={() => window.location = "/register/employee"}>
                                    Зарегистрировать сотрудника
                                </Button>
                            </Row>
                        }
                    </Card>
                </Row>
            </Container>
        </div>
    );
};

export default Profile;