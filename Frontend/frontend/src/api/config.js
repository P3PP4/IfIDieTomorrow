//requests 각 프로퍼티의 key는 API 명세서 상 'config.js상 이름'으로 명시
//각 프로퍼티의 key 주석으로 API 이름 명시(API 이름은 전부 대문자로)

const requests = {
  //기본 URL 주소
  base_url: 'https://ifidietomorrow.co.kr/api',
  //카카오 소셜로그인 URL
  KAKAO_LOGIN:
    // 'https://ifidietomorrow.co.kr/api/oauth2/authorization/kakao?redirect_uri=https://ifidietomorrow.co.kr/',
    // 'https://ifidietomorrow.co.kr/api/oauth2/authorization/kakao?redirect_uri=http://localhost:3000/',
    'https://ifidietomorrow.co.kr/api/swagger',

  //구글 소셜로그인 URL
  NAVER_LOGIN:
    // 'https://ifidietomorrow.co.kr/api/oauth2/authorization/naver?redirect_uri=https://ifidietomorrow.co.kr/',
    'https://ifidietomorrow.co.kr/api/oauth2/authorization/google?redirect_uri=http://localhost:3000/',

  //로그아웃
  GET_LOGOUT(userId) {
    return `/api/user/logout/${userId}`;
  },
  GET_USER() {
    return `/api/user`;
  },
};

export default requests;