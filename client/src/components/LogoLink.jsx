import {Col, Image} from "react-bootstrap";
import logo from "../workus_whiten.png";
import React from "react";
import {Link} from "react-router-dom";

const LogoLink = () => {
    return (
        <Col md={3}>
            <Link to={"/"}>
                <Image src={logo} fluid className={"h-50"}></Image>
            </Link>
        </Col>
    );
};

export default LogoLink;