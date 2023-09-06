import base from "./baseService";

const userUrl = "/users";
const instance = base.service(true);
export const getUsers = () => {
  return instance.get(`${userUrl}/chat`);
};

const userService = {
  getUsers,
};

export default userService;
