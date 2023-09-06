import base from "./baseService";

const authUrl = "/api/auth";
const instance = base.service(false);
const tokenInstance = base.service(true);
export const register = (request) => {
  return instance.post(`${authUrl}/register`, request);
};
export const login = (request) => {
  return instance.post(`${authUrl}/login`, request);
};
export const getUserId = () => {
  return tokenInstance.get(`${authUrl}/user-id`);
};
const CheckIfAuthorized = (id) => {
  if (id === 0) {
    return false;
  } else {
    return true;
  }
};

const authService = {
  register,
  login,
  CheckIfAuthorized,
  getUserId,
};

export default authService;
