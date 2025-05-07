import api from './axios.js'

export const fetchPointHistory = async (page = 1) => {
    try {
        const response = await api.get('/feature/purchase/point/history', {
            params: { page }
        });

        if (response.data?.success) {
            return {
                histories: response.data.data.histories,
                pagination: response.data.data.pagination
            };
        } else {
            throw new Error('응답 형식 오류');
        }
    } catch (error) {
        console.error('포인트 내역 조회 실패:', error);
        throw error;
    }
};


export const fetchDiamondHistory = async (page = 1) => {
    try {
        // const token = localStorage.getItem('accessToken'); // 또는 Pinia, Vuex에서 가져오기
        const response = await api.get('/feature/purchase/diamond/history', {
            params: { page }
        });

        if (response.data?.success) {
            return {
                histories: response.data.data.histories,
                pagination: response.data.data.pagination
            };
        } else {
            throw new Error('응답 형식 오류');
        }
    } catch (error) {
        console.error('다이아 내역 조회 실패:', error);
        throw error;
    }
};

export const fetchSaleHistory = async (page = 1) => {
    try {
        const response = await api.get('/feature/purchase/sale/history', {
            params: { page }
        });

        if (response.data?.success) {
            return {
                histories: response.data.data.saleHistories,
                pagination: response.data.data.pagination
            };
        } else {
            throw new Error('응답 형식 오류');
        }
    } catch (error) {
        console.error('판매 내역 조회 실패:', error);
        throw error;
    }
};

export const fetchSettlementHistory = async (page = 1) => {
    try {
        const response = await api.get('/feature/settlements/my', {
            params: { page }
        });

        if (response.data?.success) {
            return {
                histories: response.data.data.settlements,
                pagination: response.data.data.pagination
            };
        } else {
            throw new Error('응답 형식 오류');
        }
    } catch (error) {
        console.error('정산 내역 조회 실패:', error);
        throw error;
    }
};
