import axios from "axios";
import * as mockApi from "../mocks/mockApi";

const USE_MOCK = process.env.REACT_APP_USE_MOCK === "true";

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 토큰 설정
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("jwt_token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 토큰 에러 설정
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.warn("토큰이 만료되었거나 인증되지 않았습니다.");
      // 로그아웃 처리 필요
    }
    return Promise.reject(error);
  }
);

// mockData 사용하는 경우
const mockClient = {
  get: async (url, config) => {
    console.log(`[MOCK] GET ${url}`, config);

    const params = config?.params || {};

    if (url === "/gacha/status") {
      return { data: await mockApi.mockGachaStatus() };
    } else if (url === "/villages") {
      return { data: await mockApi.mockGetVillages(params) };
    } else if (url.startsWith("/villages/")) {
      const villageId = url.split("/")[2];
      return { data: await mockApi.mockGetVillageById(villageId) };
    } else if (url === "/collections") {
      return { data: await mockApi.mockGetCollections(params) };
    } else if (url === "/collections/stats") {
      return { data: await mockApi.mockGetCollectionStats() };
    } else if (url === "/memories") {
      return { data: await mockApi.mockGetMemories(params) };
    } else if (url.startsWith("/memories/")) {
      const memoryId = url.split("/")[2];
      return { data: await mockApi.mockGetMemoryById(memoryId) };
    } else if (url === "/users/me") {
      return { data: await mockApi.mockGetUserInfo() };
    }

    throw new Error(`Mock API: Unhandled GET ${url}`);
  },

  post: async (url, data, config) => {
    console.log(`[MOCK] POST ${url}`, data);

    if (url === "/auth/signup") {
      return { data: await mockApi.mockAuthSignup(data) };
    } else if (url === "/auth/login") {
      return { data: await mockApi.mockAuthLogin(data) };
    } else if (url === "/gacha/draw") {
      return { data: await mockApi.mockGachaDraw(data) };
    } else if (url === "/collections") {
      return { data: await mockApi.mockAddCollection(data) };
    } else if (url === "/memories") {
      return { data: await mockApi.mockCreateMemory(data) };
    }

    throw new Error(`Mock API: Unhandled POST ${url}`);
  },

  put: async (url, data, config) => {
    console.log(`[MOCK] PUT ${url}`, data);

    if (url === "/users/me") {
      return { data: await mockApi.mockUpdateUserInfo(data) };
    } else if (url.startsWith("/memories/")) {
      const memoryId = url.split("/")[2];
      return { data: await mockApi.mockUpdateMemory(memoryId, data) };
    }

    throw new Error(`Mock API: Unhandled PUT ${url}`);
  },

  delete: async (url, config) => {
    console.log(`[MOCK] DELETE ${url}`);

    if (url.startsWith("/collections/")) {
      const collectionId = url.split("/")[2];
      return { data: await mockApi.mockDeleteCollection(collectionId) };
    } else if (url.startsWith("/memories/")) {
      const memoryId = url.split("/")[2];
      return { data: await mockApi.mockDeleteMemory(memoryId) };
    }

    throw new Error(`Mock API: Unhandled DELETE ${url}`);
  },
};

const client = USE_MOCK ? mockClient : apiClient;

if (USE_MOCK) {
  console.log("🎭 Using MOCK API");
} else {
  console.log("🌐 Using REAL API");
}

export default client;
