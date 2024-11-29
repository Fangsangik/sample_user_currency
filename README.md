# user_currency
## 🥅 Period : 2024/11/26 ~ 2024/11/29
## 🛠️ Tools : <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=github&logoColor=Green"> <img alt="Java" src ="https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"/>  <img alt="Java" src ="https://img.shields.io/badge/intellijidea-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white"/>

## 👨‍💻 ERD 
![ERD](https://github.com/user-attachments/assets/7593c7b1-3609-472a-bb77-bf1858a49a07)
## 👨‍💻 API 
<a href> https://www.postman.com/gold-robot-131519/myapi/documentation/u22g0fu/exchanges?workspaceId=c64232d4-fdd3-46da-b127-41e93826dc0a</a-href>
## 👨‍💻 About Project 
- User 
  - Service : CRUD
  - 사용자 삭제시, 환전과 관련된 DB도 같이 삭제 (cascade = REMOVE 처리)
  - UserController를 통해 CRUD 호출 

- Currency
  - Service : CRU 
  - 통화 종류에 따라 소수점 나눔 or 버림
  - 통화 종류는 Enum 으로 처리
  - Currency Validation : PostConstruct를 통헤 DB 값에 잘못 들어간 값들이 있다면 log를 통해 표출 하도록 함
  - CurrencyName을 따로 Enum으로 표기해 통화와 심볼을 표기 

- Exchange 
  - Service : CRU 
  - User와 Exchange의 중간 테이블
  - Status의 enum 처리 
   
## 🥵 Trouble Shooting
- BigDecimal.setScale을 사용해 소수점 이하를 버리거나 반올림 하려고 했는데 버림이 적용이 안됨 
  - 소수점을 제대로 처리 하기위해 RoundingMode와 정밀도 scale을 사용 

- 통화별로 소수점 처리 여부 관리 필요 
```
int scale = isWholeNumber ? 0 : 2;
RoundingMode roundingMode = isWholeNumber ? RoundingMode.DOWN : RoundingMode.HALF_UP;
BigDecimal result = amount.divide(exchangeRate, scale, roundingMode); 
``` 
- 한개의 UserId로 여러번 update 실행했을때 getSingleResult로 반환해 문제 발생
  - update 쿼리를 실행후, 업데이트 된 데이터 조회 하는 방향으로 설계 

- update시 소수점 적용 안되는 문제와 최종적으로 보여져야 하는 환전 값 + symbol이 나오지 않음 
  - formatter라는 interface를 따로 빼서 작성 
  - 그리고 각 메서드 마다 foramtter 기능을 추가해 값 + symbol이 보여지게 만듬 
  - 소수점 처리하는 기능을 추가 안함 
## 🚀 Refactoring
- 소수점 계산 로직 처리 
```
    @Transactional
    @Override
    public BigDecimal calculateExchangeAmount(BigDecimal amountInKrw, BigDecimal exchangeRate, boolean isWholeNumber) {
        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new CustomError(INVALID_CURRENCY_EXCEPTION);
        }
        int scale = isWholeNumber ? 0 : 2;
        RoundingMode roundingMode = isWholeNumber ? RoundingMode.DOWN : RoundingMode.HALF_DOWN;
        return amountInKrw.divide(exchangeRate, scale, roundingMode);
   }
```
처음에는 Service 로직에서 처리를 시도 했다. 

```
public abstract class ExchangeCalculator {

    public BigDecimal calculateExchangeAmount(BigDecimal amountInKrw, BigDecimal exchangeRate, boolean isWholeNumber) {
        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new CustomError(INVALID_CURRENCY_EXCEPTION);
        }

        int scale = getScale(isWholeNumber);
        RoundingMode roundingMode = getRoundingMode(isWholeNumber);

        return amountInKrw.divide(exchangeRate, scale, roundingMode);
    }

    protected abstract int getScale(boolean isWholeNumber);

    protected abstract RoundingMode getRoundingMode(boolean isWholeNumber);
}
```
나중에 추상 클래스로 적용 
이유 : 
- 단순 계산 로직 이라고 생각해 service단 에서 있어야 된다고 생각이 안들었다. 단순 계산 기능만 하는 거라면 추상클래스로 빼서 생각해도 괜찮지 않을까? 
   
## 👨‍💻 시도한 것과 이유 & 궁금증 
1.  추상 클래스로 적용한 이유 

  - 1) ExchangeCalculator

이번 프로젝트를 적용하면서 확장 가능성과 변경 가능성에 열어두고 작성을 해보았습니다. 
적용해본 예로는 소수점 처리 계산 부분입니다. 
abstract로 빼 2째 자리로 처리해도 되고 만약 처리 하는 방법이 2째 자리를 두고 다른 통화의 경우 3째자리 까지만 변경하라고 한다면 그 부분만 작성하면 된다고 생각했습니다. 
또다른 서비스 로직을 작성할 필요 없이 소수점 처리만 변경 혹은 작성하면 된다고 생각해 abstract class로 빼 보았습니다. 
그리고 ExchangeCalculator는 단순 소수점 처리만 하는 기능이기 때문에 Service에 있는 것 보다는 따로 기능을 빼서 처리하는게 좋다고 판단이 되었습니다. 

  - 2) ExchangeFormatter

인터패이스로 ExchangeForamtter라는 값을 갖고, ExchangeForamtterImpl을 정의했다. 
CurrencyName을 갖고 true일 경우 버리고, 아닐 경우 2째 자리 까지만 표기 하는 기능 
ExchangeForamtter 또한 단순 변환 기능 이기에 추상 클래스가 좋을까 인터페이스가 좋을까에 대한 고민이 있었습니다.
나중에 데이터 변환이라는 동작이 다양한 구현을 지원하는 경우를 생각해 작성했고, 상태를 갖기 보다는 순수 기능 중심일 것 같다는 생각이 들었습니다. 

고민한 점은 단순 기능이라면 springBean으로 등록을 해줄 필요가 있을까 라는 의문점이 들었습니다. 
이 기능들을 단순 정적 유틸 클래스로 처리하는 방법도 좋았지 않았을까? 라는 궁금증도 있었습니다. 

2. toEntiy toDto 

정적 메서드 처리 
Mapstrcut와 Componet로 등록해 빈으로 관리했던 방법과는 달리, 정적 메서드로 처리했습니다. 

3. Builder 패턴을 생성자에 처리
 
예전 프로젝트들은 빌더 패턴을 class level에 처리해 원하지 않는 entity의 id 값 까지 Builder에 포함되는 경우가 있었습니다. 
하지만 생성자 level에 처리해 builder에 대한 제약이라면 제약일 수 있는 것을 걸어두어 사용했습니다. 

4. RequestDto와 ResponseDto
 
DTO는 데이터를 전달하고, 검증하는 것의 역할이지 모든 데이터를 갖고 있을 필요는 없다고 생각이 들었습니다. 
이전에는 DTO에 모든 데이터가 들어가있어야 한고, 보여줄때만 ResponseDTO에서 PostMan을 호출할때 걸러서 보여주면 된다고 생각했지만 그게 아닌,
필요한 데이터만 들어가면 된다고 생각이 바뀌어 DTO의 필드 값을 제약적으로 사용했습니다.

5. PostConstruct의 기능에 필요성의 의문
 
PostConstruct는 기존 DB에 있는 값이 잘못된 값이 있는지 없는지를 Spring이 시작 된후 DB에 접근해 잘못된 값이 있다면 
log를 통해 보여주는 것으로 알고 있습니다. 
하지만 애초에 서비스 로직에서 DB에 들어가는 값의 검증 부분을 적절히 처리한다면 필요성에 대한 의문이 들었습니다. 
실무에서는 어떤 방향으로 사용하는지도 궁금합니다.

6. CustomError에 대한 궁금증 

지금은 CustomError라는 클래스 하나에 ErrorType이 적용 되어 잘못된 값이 호출이 되면 내가 적용한 ErrorType을 사용자에게 보여주는 방향으로 설계가 되어있다 
CustomError를 상황별로 나누는게 맞을까 라는 궁금증이 생겼다 
예를 들어 NotFoundError, InvalidInputError등 CustomError를 만들어 RuntimeException을 각각 상속받아 공통 ErrorType을 처리하는게 좋은지, 
아니면 내가 작성한 방법으로만 해도  충분할까 라는 의문이 들었다. 
설계 방향에 따라 달라질 수는 있겠지만, 현업에서는 나누는지 아니면 공통된 CustomError로 처리해서 가는지에 대한 궁금증이 있다. 

7. AllargsConstructor와 NoArgsConstructor RequiredArgConsturctor

AllargsConstructor와 NoArgsConstructor, RequiredArgConsturctor를 Annotation을 따로 적용하지 않고, 직접 필요한 생성자를 생성해 사용했습니다.
항상 자동에 의존하는 것 보다는 직접 제가 제어 할 수 있는 방향으로 code를 작성하는 것이 좋다는 생각이 들어 이번에는 직접 눈으로 확인할 수 있는 방향으로
Code를 작성했습니다. 

## 다음 프로젝트때 시도 해 볼것들 
1. Set의 사용 

불변성을 해친다고해 사용하면 좋지 않다는 이야기를 들었다. 하지만 set을 사용하기 이전에 사전에 불변성을 처리해준다면 set을 사용해도 되지 않을까 라는 생각이 들었다.

2. Builder의 커스텀화
 
이번 프로젝트때는 Builder를 생성자 level에 처리했지만, 다음번에는 Builder를 직접 만들어 사용해보고 싶다는 생각이 들었다 

3. interface와 Abstract를 좀더 적극 활용해보기 

어떤 기능을 확장 가능성을 여러두고 작성하는 일은 어렵다. 하지만 시도는 해보고 싶다. 

4. 동시성 문제 고려해보기 

낙관적 락과, 비관적 락의 차이를 공부하고, 동시성이 문제가 될만한 기능들을 고려해 적용해보고 싶다.    


