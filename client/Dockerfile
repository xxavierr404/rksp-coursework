FROM node:20-alpine
WORKDIR /app
ARG REACT_APP_SERVER_IP
ENV REACT_APP_SERVER_IP=$REACT_APP_SERVER_IP
COPY package.json .
RUN npm install
COPY . .
CMD ["npm", "start"]