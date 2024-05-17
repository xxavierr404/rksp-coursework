import React, {useCallback, useEffect, useState} from 'react';
import {Button, Card, Col, Container, Form, Modal, Row} from "react-bootstrap";
import LogoLink from "./LogoLink";
import Cookies from "js-cookie";
import axios from "axios";
import {Link, useParams} from "react-router-dom";
import ChatCard from "./ChatCard";
import {Stomp} from "@stomp/stompjs";
import ChatMessage from "./ChatMessage";
import {StompSessionProvider} from "react-stomp-hooks";
import Chat from "./Chat";

const ChatStompContextWrapper = () => {
    const authHeader = {
        "X-Authorization": `Bearer ${Cookies.get("workus-tkn")}`
    };

    return (
        <StompSessionProvider url={`ws://${process.env.REACT_APP_SERVER_IP}:5555/api/v1/messaging`} connectHeaders={authHeader}>
            <Chat></Chat>
        </StompSessionProvider>
    );
};

export default ChatStompContextWrapper;