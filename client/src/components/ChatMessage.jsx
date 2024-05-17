import React, {useEffect, useState} from 'react';
import {Card, Col, Container, Row} from "react-bootstrap";
import axios from "axios";
import Cookies from "js-cookie";
import {Link} from "react-router-dom";

const ChatMessage = (props) => {
    const [authorName, setAuthorName] = useState("");

    useEffect(() => {
        updateAuthorName();
    }, []);

    const updateAuthorName = async () => {
        await axios.get(
            `http://localhost:5552/api/v1/user-profile/${props.author}`,
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
            .then(resp => setAuthorName(resp.data.firstName));
    }

    return (
        <Card className={"bg-black justify-content-start w-100 my-2"}>
            <Container className={"pb-2"}>
                <Row className={"text-md fs-4 text-white justify-content-between"}>
                    <Col>
                        <Link to={`/profile/${props.author}`}>{authorName}</Link>
                    </Col>
                    <Col md={3} className={"text-md-end"}>
                        {new Date(props.time).toLocaleTimeString("ru-RU")}
                    </Col>
                </Row>
                <Row className={"text-md fs-6 text-white justify-content-start"}>
                    <Col>
                        {props.text}
                    </Col>
                </Row>
            </Container>
        </Card>
    );
};

export default ChatMessage;