# SASA Messenger

#### 실행 방법
- 종속 패키지를 설치하기 위해, View > Tool Windows > Gradle로 이동하여 buildDependents Task를 실행합니다. (클릭)
- com.simotion.talk.Main을 실행합니다.

#### 알려진 문제
- 채팅을 보낸 후, 이전 메세지가 깨져서 보여지는 경우가 있습니다.
- 채팅을 보냈을 때, 메세지가 두 번 표시되는 경우가 있습니다.

로컬 데이터베이스, 네트워킹 등의 문제는 아닌 것으로 추정되기 때문에, 이 문제들은 모두 JavaFX의 버그로 인해 발생하는 것으로 추정됩니다.

관련 버그 리포트: https://bugs.openjdk.java.net/browse/JDK-8172693
