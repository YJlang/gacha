import {
  villages,
  users,
  initialCollections,
  initialMemories,
  initialGachaHistory,
} from "./mockData";

// Helper function to get data from localStorage
const getFromStorage = (key, defaultValue) => {
  const data = localStorage.getItem(key);
  return data ? JSON.parse(data) : defaultValue;
};

// Helper function to save data to localStorage
const saveToStorage = (key, value) => {
  localStorage.setItem(key, JSON.stringify(value));
};

// Initialize storage
const initializeStorage = () => {
  if (!localStorage.getItem("mock_users")) {
    saveToStorage("mock_users", users);
  }
  if (!localStorage.getItem("mock_collections")) {
    saveToStorage("mock_collections", initialCollections);
  }
  if (!localStorage.getItem("mock_memories")) {
    saveToStorage("mock_memories", initialMemories);
  }
  if (!localStorage.getItem("mock_gacha_history")) {
    saveToStorage("mock_gacha_history", initialGachaHistory);
  }
};

initializeStorage();

// Simulate network delay
const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

// Helper to create success response
const successResponse = (data, message = "") => ({
  success: true,
  message,
  data,
});

// Helper to create error response
const errorResponse = (message, error) => ({
  success: false,
  message,
  error,
});

// ==================== Auth API ====================

export const mockAuthSignup = async (userData) => {
  await delay();
  const users = getFromStorage("mock_users", []);

  // Check if username already exists
  if (users.find((u) => u.username === userData.username)) {
    throw {
      response: {
        data: errorResponse(
          "이미 존재하는 아이디입니다.",
          "USERNAME_ALREADY_EXISTS"
        ),
      },
    };
  }

  const newUser = {
    userId: users.length + 1,
    username: userData.username,
    password: userData.password,
    email: userData.email,
    createdAt: new Date().toISOString(),
  };

  users.push(newUser);
  saveToStorage("mock_users", users);

  return successResponse(
    {
      userId: newUser.userId,
      username: newUser.username,
      email: newUser.email,
    },
    "회원가입이 완료되었습니다."
  );
};

export const mockAuthLogin = async (credentials) => {
  await delay();
  const users = getFromStorage("mock_users", []);

  const user = users.find(
    (u) =>
      u.username === credentials.username && u.password === credentials.password
  );

  if (!user) {
    throw {
      response: {
        data: errorResponse(
          "아이디 또는 비밀번호가 올바르지 않습니다.",
          "INVALID_CREDENTIALS"
        ),
      },
    };
  }

  // Generate mock JWT token
  const token = `mock_jwt_${user.userId}_${Date.now()}`;

  // Store current user info
  saveToStorage("mock_current_user", user);

  return successResponse(
    {
      token,
      user: {
        userId: user.userId,
        username: user.username,
        email: user.email,
      },
    },
    "로그인 성공"
  );
};

// ==================== Gacha API ====================

export const mockGachaStatus = async () => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const gachaHistory = getFromStorage("mock_gacha_history", []);
  const today = new Date().toDateString();

  const todayHistory = gachaHistory.filter(
    (h) =>
      h.userId === currentUser.userId &&
      new Date(h.drawnAt).toDateString() === today
  );

  const canDraw = todayHistory.length === 0;
  const lastDraw = todayHistory.length > 0 ? todayHistory[0] : null;

  return successResponse({
    canDraw,
    remainingCount: canDraw ? 1 : 0,
    lastDrawTime: lastDraw ? lastDraw.drawnAt : null,
  });
};

export const mockGachaDraw = async (filters = {}) => {
  await delay(500);
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const gachaHistory = getFromStorage("mock_gacha_history", []);
  const today = new Date().toDateString();

  const todayHistory = gachaHistory.filter(
    (h) =>
      h.userId === currentUser.userId &&
      new Date(h.drawnAt).toDateString() === today
  );

  if (todayHistory.length > 0) {
    throw {
      response: {
        data: errorResponse(
          "오늘 가챠 횟수를 모두 사용했습니다. 내일 다시 시도해주세요.",
          "DAILY_LIMIT_EXCEEDED"
        ),
        status: 429,
      },
    };
  }

  // Filter villages based on criteria
  let availableVillages = villages;
  if (filters.region) {
    availableVillages = availableVillages.filter(
      (v) => v.sidoName === filters.region
    );
  }
  if (filters.programType) {
    availableVillages = availableVillages.filter((v) =>
      v.programName.includes(filters.programType)
    );
  }

  // Pick random village
  const randomVillage =
    availableVillages[Math.floor(Math.random() * availableVillages.length)];

  // Check if user already collected this village
  const collections = getFromStorage("mock_collections", []);
  const isNew =
    !collections.find(
      (c) =>
        c.userId === currentUser.userId &&
        c.villageId === randomVillage.villageId
    );

  // Save to gacha history
  const newGacha = {
    gachaId: gachaHistory.length + 1,
    userId: currentUser.userId,
    villageId: randomVillage.villageId,
    drawnAt: new Date().toISOString(),
  };
  gachaHistory.push(newGacha);
  saveToStorage("mock_gacha_history", gachaHistory);

  return successResponse(
    {
      ...randomVillage,
      isNew,
      drawnAt: newGacha.drawnAt,
    },
    "가챠 뽑기 성공"
  );
};

// ==================== Villages API ====================

export const mockGetVillages = async (params = {}) => {
  await delay();
  const page = params.page || 0;
  const size = params.size || 20;

  let filteredVillages = villages;

  // Apply filters
  if (params.region) {
    filteredVillages = filteredVillages.filter(
      (v) => v.sidoName === params.region
    );
  }
  if (params.programType) {
    filteredVillages = filteredVillages.filter((v) =>
      v.programName.includes(params.programType)
    );
  }

  // Pagination
  const totalElements = filteredVillages.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const end = start + size;
  const content = filteredVillages.slice(start, end);

  // Check if collected
  const currentUser = getFromStorage("mock_current_user", null);
  const collections = getFromStorage("mock_collections", []);

  const contentWithCollection = content.map((village) => ({
    villageId: village.villageId,
    villageName: village.villageName,
    sidoName: village.sidoName,
    sigunguName: village.sigunguName,
    address: village.address,
    isCollected: currentUser
      ? collections.some(
          (c) =>
            c.userId === currentUser.userId &&
            c.villageId === village.villageId
        )
      : false,
  }));

  return successResponse({
    content: contentWithCollection,
    totalElements,
    totalPages,
    currentPage: page,
    size,
  });
};

export const mockGetVillageById = async (villageId) => {
  await delay();
  const village = villages.find((v) => v.villageId === parseInt(villageId));

  if (!village) {
    throw {
      response: {
        data: errorResponse("여행지를 찾을 수 없습니다.", "VILLAGE_NOT_FOUND"),
        status: 404,
      },
    };
  }

  const currentUser = getFromStorage("mock_current_user", null);
  const collections = getFromStorage("mock_collections", []);

  const collection = currentUser
    ? collections.find(
        (c) =>
          c.userId === currentUser.userId &&
          c.villageId === village.villageId
      )
    : null;

  return successResponse({
    ...village,
    isCollected: !!collection,
    collectedAt: collection ? collection.collectedAt : null,
  });
};

// ==================== Collections API ====================

export const mockGetCollections = async (params = {}) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const page = params.page || 0;
  const size = params.size || 20;

  const collections = getFromStorage("mock_collections", []);
  const userCollections = collections.filter(
    (c) => c.userId === currentUser.userId
  );

  // Add village details
  const collectionsWithDetails = userCollections.map((c) => {
    const village = villages.find((v) => v.villageId === c.villageId);
    return {
      collectionId: c.collectionId,
      villageId: c.villageId,
      villageName: village?.villageName,
      sidoName: village?.sidoName,
      sigunguName: village?.sigunguName,
      address: village?.address,
      collectedAt: c.collectedAt,
    };
  });

  // Pagination
  const totalElements = collectionsWithDetails.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const end = start + size;
  const content = collectionsWithDetails.slice(start, end);

  return successResponse({
    content,
    totalElements,
    totalPages,
    currentPage: page,
    size,
  });
};

export const mockAddCollection = async (data) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const collections = getFromStorage("mock_collections", []);

  // Check if already collected
  const existing = collections.find(
    (c) =>
      c.userId === currentUser.userId && c.villageId === data.villageId
  );

  if (existing) {
    throw {
      response: {
        data: errorResponse(
          "이미 컬렉션에 추가된 여행지입니다.",
          "ALREADY_COLLECTED"
        ),
        status: 400,
      },
    };
  }

  const village = villages.find((v) => v.villageId === data.villageId);
  if (!village) {
    throw {
      response: {
        data: errorResponse("여행지를 찾을 수 없습니다.", "VILLAGE_NOT_FOUND"),
        status: 404,
      },
    };
  }

  const newCollection = {
    collectionId: collections.length + 1,
    userId: currentUser.userId,
    villageId: data.villageId,
    collectedAt: new Date().toISOString(),
  };

  collections.push(newCollection);
  saveToStorage("mock_collections", collections);

  return successResponse(
    {
      collectionId: newCollection.collectionId,
      villageId: newCollection.villageId,
      villageName: village.villageName,
      collectedAt: newCollection.collectedAt,
    },
    "컬렉션에 추가되었습니다."
  );
};

export const mockDeleteCollection = async (collectionId) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const collections = getFromStorage("mock_collections", []);
  const index = collections.findIndex(
    (c) =>
      c.collectionId === parseInt(collectionId) &&
      c.userId === currentUser.userId
  );

  if (index === -1) {
    throw {
      response: {
        data: errorResponse(
          "컬렉션을 찾을 수 없습니다.",
          "COLLECTION_NOT_FOUND"
        ),
        status: 404,
      },
    };
  }

  collections.splice(index, 1);
  saveToStorage("mock_collections", collections);

  return successResponse({}, "컬렉션에서 제거되었습니다.");
};

export const mockGetCollectionStats = async () => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const collections = getFromStorage("mock_collections", []);
  const userCollections = collections.filter(
    (c) => c.userId === currentUser.userId
  );

  const regionStats = {};
  userCollections.forEach((c) => {
    const village = villages.find((v) => v.villageId === c.villageId);
    if (village) {
      regionStats[village.sidoName] =
        (regionStats[village.sidoName] || 0) + 1;
    }
  });

  return successResponse({
    totalCount: userCollections.length,
    regionStats,
  });
};

// ==================== Memories API ====================

export const mockGetMemories = async (params = {}) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const page = params.page || 0;
  const size = params.size || 20;

  const memories = getFromStorage("mock_memories", []);
  const userMemories = memories.filter((m) => m.userId === currentUser.userId);

  // Add village details
  const memoriesWithDetails = userMemories.map((m) => {
    const village = villages.find((v) => v.villageId === m.villageId);
    return {
      memoryId: m.memoryId,
      villageId: m.villageId,
      villageName: village?.villageName,
      sidoName: village?.sidoName,
      sigunguName: village?.sigunguName,
      address: village?.address,
      content: m.content,
      visitDate: m.visitDate,
      createdAt: m.createdAt,
    };
  });

  // Pagination
  const totalElements = memoriesWithDetails.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const end = start + size;
  const content = memoriesWithDetails.slice(start, end);

  return successResponse({
    content,
    totalElements,
    totalPages,
    currentPage: page,
    size,
  });
};

export const mockGetMemoryById = async (memoryId) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const memories = getFromStorage("mock_memories", []);
  const memory = memories.find(
    (m) =>
      m.memoryId === parseInt(memoryId) && m.userId === currentUser.userId
  );

  if (!memory) {
    throw {
      response: {
        data: errorResponse("추억을 찾을 수 없습니다.", "MEMORY_NOT_FOUND"),
        status: 404,
      },
    };
  }

  const village = villages.find((v) => v.villageId === memory.villageId);

  return successResponse({
    memoryId: memory.memoryId,
    villageId: memory.villageId,
    villageName: village?.villageName,
    sidoName: village?.sidoName,
    sigunguName: village?.sigunguName,
    address: village?.address,
    content: memory.content,
    visitDate: memory.visitDate,
    createdAt: memory.createdAt,
    updatedAt: memory.updatedAt,
  });
};

export const mockCreateMemory = async (data) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const village = villages.find((v) => v.villageId === data.villageId);
  if (!village) {
    throw {
      response: {
        data: errorResponse("여행지를 찾을 수 없습니다.", "VILLAGE_NOT_FOUND"),
        status: 404,
      },
    };
  }

  const memories = getFromStorage("mock_memories", []);
  const newMemory = {
    memoryId: memories.length + 1,
    userId: currentUser.userId,
    villageId: data.villageId,
    content: data.content,
    visitDate: data.visitDate || new Date().toISOString().split("T")[0],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  memories.push(newMemory);
  saveToStorage("mock_memories", memories);

  return successResponse(
    {
      memoryId: newMemory.memoryId,
      villageId: newMemory.villageId,
      villageName: village.villageName,
      content: newMemory.content,
      visitDate: newMemory.visitDate,
      createdAt: newMemory.createdAt,
    },
    "추억이 저장되었습니다."
  );
};

export const mockUpdateMemory = async (memoryId, data) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const memories = getFromStorage("mock_memories", []);
  const memory = memories.find(
    (m) =>
      m.memoryId === parseInt(memoryId) && m.userId === currentUser.userId
  );

  if (!memory) {
    throw {
      response: {
        data: errorResponse("추억을 찾을 수 없습니다.", "MEMORY_NOT_FOUND"),
        status: 404,
      },
    };
  }

  memory.content = data.content;
  if (data.visitDate) {
    memory.visitDate = data.visitDate;
  }
  memory.updatedAt = new Date().toISOString();

  saveToStorage("mock_memories", memories);

  return successResponse(
    {
      memoryId: memory.memoryId,
      content: memory.content,
      visitDate: memory.visitDate,
      updatedAt: memory.updatedAt,
    },
    "추억이 수정되었습니다."
  );
};

export const mockDeleteMemory = async (memoryId) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const memories = getFromStorage("mock_memories", []);
  const index = memories.findIndex(
    (m) =>
      m.memoryId === parseInt(memoryId) && m.userId === currentUser.userId
  );

  if (index === -1) {
    throw {
      response: {
        data: errorResponse("추억을 찾을 수 없습니다.", "MEMORY_NOT_FOUND"),
        status: 404,
      },
    };
  }

  memories.splice(index, 1);
  saveToStorage("mock_memories", memories);

  return successResponse({}, "추억이 삭제되었습니다.");
};

// ==================== User API ====================

export const mockGetUserInfo = async () => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const collections = getFromStorage("mock_collections", []);
  const memories = getFromStorage("mock_memories", []);

  const collectionCount = collections.filter(
    (c) => c.userId === currentUser.userId
  ).length;
  const memoryCount = memories.filter(
    (m) => m.userId === currentUser.userId
  ).length;

  return successResponse({
    userId: currentUser.userId,
    username: currentUser.username,
    email: currentUser.email,
    collectionCount,
    memoryCount,
    createdAt: currentUser.createdAt,
  });
};

export const mockUpdateUserInfo = async (data) => {
  await delay();
  const currentUser = getFromStorage("mock_current_user", null);
  if (!currentUser) {
    throw {
      response: {
        data: errorResponse("인증이 필요합니다.", "UNAUTHORIZED"),
        status: 401,
      },
    };
  }

  const users = getFromStorage("mock_users", []);
  const user = users.find((u) => u.userId === currentUser.userId);

  if (user && data.email) {
    user.email = data.email;
    currentUser.email = data.email;
  }

  saveToStorage("mock_users", users);
  saveToStorage("mock_current_user", currentUser);

  return successResponse(
    {
      userId: currentUser.userId,
      username: currentUser.username,
      email: currentUser.email,
    },
    "정보가 수정되었습니다."
  );
};
