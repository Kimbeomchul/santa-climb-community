# santa 이슈

### INFO
- Design : Figma ( https://www.figma.com/file/Dnxgef8SlcPSEClgHF2w1g/%EB%AA%A8%EC%85%94%EC%96%B8?node-id=0%3A1 )
- Domain = // 2024.07.12 만료 

### TODO 
- 뒤로가기 History 날리기 -> Flutter에서 메인/게시판/채팅/프로필화면에서만 처리하고 나머지는 히스토리 남기도록 변경하여 해결
- 등산페이지 개발 - /map/climb -> 등산기록 0.001KM 식으로 거리계산 및 해당등산기록 마이페이지에 저장하도록 변경함// 지도까지 볼수있도록저장함 ( 지도 스크린샷의 경우 카카오맵 저작권으로 불가 ) 

- 채팅 지속 리스너개발해야함 
- 채팅 노티 / Android Wakeup개발 알람기능 추가해야함
- 등산하기 위치데이터 js 받기처리 해야함 ( 등산하기 클릭시 앱 포어그라운드 처리로 해결 (위치정보제공 항상일때만 가능))
- 구글정책수정

### Jenkins 
- ERROR: Couldn't find any revision to build. Verify the repository and branch configuration for this job.
*/master -> */main 으로 브랜치 변경 후 재시도
