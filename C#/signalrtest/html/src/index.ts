import * as signalR from "@microsoft/signalr";
import "./css/main.css";

const divMessages: HTMLDivElement = document.querySelector("#divMessages");
const tbMessage: HTMLInputElement = document.querySelector("#tbMessage");
const btnSend: HTMLButtonElement = document.querySelector("#btnSend");
const username = new Date().getTime();

console.log()

const connection = new signalR.HubConnectionBuilder()
    .withUrl("http://danggui.xyz:5011/chatHub")
    .build();

connection.on("ReceiveMessage", (username: string, message: string) => {
  const op: ElementCreationOptions = {
    is: 'hfkjahsdkgj'
  }

  const m = document.createElement("div", op);

  m.innerHTML = `<div class="message-author">${username}</div><div>${message}</div>`;

  divMessages.appendChild(m);
  divMessages.scrollTop = divMessages.scrollHeight;
});

connection.start().catch((err) => document.write(err));

tbMessage.addEventListener("keyup", (e: KeyboardEvent) => {
  if (e.key === "Enter") {
    send();
  }
});

btnSend.addEventListener("click", send);

function send() {
  connection.send("SendMessage", username.toString(), tbMessage.value)
    .then(() => (tbMessage.value = ""))
    .catch((err)=>{
      console.log(err)
    });
}