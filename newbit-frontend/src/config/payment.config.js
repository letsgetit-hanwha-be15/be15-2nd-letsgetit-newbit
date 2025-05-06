export const paymentConfig = {
  toss: {
    clientKey: {
      test: import.meta.env.VITE_TOSS_TEST_CLIENT_KEY,
      prod: import.meta.env.VITE_TOSS_PROD_CLIENT_KEY,
    },
  },
  urls: {
    success: "/payments/success",
    fail: "/payments/fail",
  },
};

export const getPaymentConfig = () => {
  const isDevelopment = import.meta.env.MODE === "development";

  if (!isDevelopment && !paymentConfig.toss.clientKey.prod) {
    console.warn(
      "프로덕션 환경에서 Toss Payments 클라이언트 키가 설정되지 않았습니다."
    );
  }

  return {
    ...paymentConfig,
    toss: {
      ...paymentConfig.toss,
      currentClientKey: isDevelopment
        ? paymentConfig.toss.clientKey.test
        : paymentConfig.toss.clientKey.prod,
    },
  };
};
