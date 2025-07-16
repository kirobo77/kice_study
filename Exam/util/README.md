## 주요 사용법 및 특징

- **POST /mask**

  - 입력:

    ```
    json
    { "name": "홍길동", "phone": "01012345678", "email": "hong@test.com" }
    ```

  - 응답:

    ```
    json
    { "name": "홍길동", "phone": "010****5678", "email": "h##g@test.com" }
    ```

  - 동작: 마스킹 처리 후 원본을 original_data.txt에 저장

- **POST /unmask**

  - 입력:

    ```
    json
    { "name": "홍길동" }
    ```

  - 응답:

    ```
    json
    { "name": "홍길동", "phone": "01012345678", "email": "hong@test.com" }
    ```

  - 동작: original_data.txt에서 원본을 찾아 반환

## 기능 요약표

| 엔드포인트 | 메서드 | 입력 데이터        | 처리 내용           | 응답 데이터             |
| :--------- | :----- | :----------------- | :------------------ | :---------------------- |
| /mask      | POST   | name, phone, email | 마스킹 후 원본 저장 | 마스킹된 고객정보       |
| /unmask    | POST   | name               | 원본 데이터 조회    | 원본 고객정보 또는 오류 |