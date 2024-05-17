import React, {useEffect, useState} from 'react';
import {Button, Col, Form, Row} from "react-bootstrap";
import axios from "axios";
import Cookies from "js-cookie";

const AddMemberRow = (props) => {
    const [profile, setProfile] = useState("");
    const [isManager, setIsManager] = useState(false);

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

    const invite = async () => {
        await axios.post(
            `http://${process.env.REACT_APP_SERVER_IP}:5553/api/v1/chat/${props.chatId}/${props.id}?isManager=${isManager}`,
            {},
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
                    props.updatePotentialMembers();
                }
            })
    }

    return (
        <Form.Group className={"mt-3 w-100"}>
            <Row className={"my-3"}>
                <Col sm={4} className={"text-md-center"}>
                    {profile.firstName} {profile.lastName}
                </Col>
                <Col sm={4}>
                    <Form.Group>
                        <Form.Label>Дать права менеджера?</Form.Label>
                        <Form.Check
                            required
                            type={"checkbox"}
                            onChange={event => setIsManager(event.target.checked)}
                        ></Form.Check>
                    </Form.Group>
                </Col>
                <Col sm={4}>
                    <Button variant={"success"} className={"h-100 w-100"}
                            onClick={() => invite()}>
                        Добавить
                    </Button>
                </Col>
            </Row>
        </Form.Group>
    );
};

export default AddMemberRow;