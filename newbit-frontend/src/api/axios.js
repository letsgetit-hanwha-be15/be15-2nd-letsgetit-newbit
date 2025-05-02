import axios from "axios";

export const createApiClient = (baseURL) => {
  const client = axios.create({
    baseURL,
    timeout: 10000,
    headers: {
      "Content-Type": "application/json",
    },
  });

  client.interceptors.request.use(
    (config) => {
      // TODO: 토큰이나 인증 정보 추가 필요시 여기서 처리
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  client.interceptors.response.use(
    (response) => response.data,
    (error) => {
      // TODO: 에러 처리 로직 추가 필요
      return Promise.reject(error);
    }
  );

  return client;
};
