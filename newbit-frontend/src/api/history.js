import api from './axios.js'

//테스트용 임시 토큰
const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiYXV0aG9yaXR5IjoiTUVOVE9SIiwidXNlcklkIjoxMSwiaWF0IjoxNzQ2NDQxNjE4LCJleHAiOjE3NDY0NDM0MTh9.y_Jyk5Am6sD_Q5QnNNzXUePD28srth4EdQe78Eau5tqpnov7zSk1hXouxgTHShJfm2rVICoNM3Cgj-O_ChPOvQ'

//todo: headers 공통 로직으로 제외
export const fetchPointHistory = async (page = 1) => {
    try {
        // const token = localStorage.getItem('accessToken'); // 또는 Pinia, Vuex에서 가져오기

        const response = await api.get('/feature/purchase/point/history', {
            params: { page },
            headers: {
                Authorization: `Bearer ${token}`
            }
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
            params: { page },
            headers: {
                Authorization: `Bearer ${token}`
            }
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
        // const token = localStorage.getItem('accessToken'); // 또는 Pinia, Vuex에서 가져오기
        const response = await api.get('/feature/purchase/sale/history', {
            params: { page },
            headers: {
                Authorization: `Bearer ${token}`
            }
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

