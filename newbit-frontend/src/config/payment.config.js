export const paymentConfig = {
  toss: {
    scriptUrl: "https://js.tosspayments.com/v1",
    clientKey: {
      test:
        import.meta.env.VITE_TOSS_TEST_CLIENT_KEY ||
        "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq", // TODO : .env 파일 없는 사람을 위해 하드코딩 배포 후 삭제 필요요
      prod: import.meta.env.VITE_TOSS_PROD_CLIENT_KEY,
    },
  },
  api: {
    baseUrl: import.meta.env.VITE_API_BASE_URL || "/api/v1",
    endpoints: {
      verify: "/payments/verify",
      status: "/payments",
      cancel: "/payments/cancel",
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
