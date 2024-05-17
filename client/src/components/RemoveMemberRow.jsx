import React, {useEffect, useState} from 'react';
import {Button, Col, Form, Row} from "react-bootstrap";
import axios from "axios";
import Cookies from "js-cookie";

const RemoveMemberRow = (props) => {
    const [profile, setProfile] = useState("");

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

    const kick = async () => {
        await axios.delete(
            `http://${process.env.REACT_APP_SERVER_IP}:5553/api/v1/chat/${props.chatId}/${props.id}`,
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
                    props.updateChatInfo();
                }
            })
    }

    return (
        <Form.Group className={"mt-3 w-100"}>
            <Row className={"my-3"}>
                <Col className={"text-md-center"}>
                    {profile.firstName} {profile.lastName}
                </Col>
                <Col sm={3}>
                    <Button variant={"danger"} className={"h-100 w-100"}
                            onClick={() => kick()}>
                        Выгнать
                    </Button>
                </Col>
            </Row>
        </Form.Group>
    );
};

export default RemoveMemberRow;