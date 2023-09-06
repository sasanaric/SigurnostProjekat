import base from "./baseService";

const messageUrl = "/messages";
const instance = base.service(true);
export const getUsersChat = (receiverId) => {
  return instance.get(`${messageUrl}/user/${receiverId}`);
};
export const sendMessage = (message, receiverId) => {
  return instance.post(`${messageUrl}/send/${receiverId}`, message);
};
export const getLastMessageId = (senderId) => {
  return instance.get(`${messageUrl}/user/last-message/${senderId}`);
};
const messageService = {
  getUsersChat,
  sendMessage,
};

export default messageService;
