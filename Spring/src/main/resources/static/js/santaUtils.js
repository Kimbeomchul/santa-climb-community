/**
 * 등산시작
 * @param lat
 * @param lon
 */
var climbFlag = localStorage.getItem("climb.status");
function deviceInfo(deviceInfo, deviceData) {
  // deviceInfo test
}

function climbLocation(lat, lon) {
  localStorage.setItem("climb.status", "Y");
  climbFlag = localStorage.getItem("climb.status");
  if (climbFlag) {
    // Init location
    let santaUtilClimbLocation = localStorage.getItem("climb.location");
    if (santaUtilClimbLocation) {
      santaUtilClimbLocation += "," + lat + "," + lon;
      localStorage.setItem("climb.location", santaUtilClimbLocation);
    } else {
      localStorage.setItem("climb.location", [lat, lon]);
    }
  }
}
function climbTimer() {
  if (!climbFlag) localStorage.setItem("climb.time", moment());
  let climbStartTimer = localStorage.getItem("climb.time");
  // Init timer
  setInterval(function () {
    // 현재 ms(밀리세컨) 타임에서 초를더해서 00 : 00 형식으로 표현
    let currentClimbTime = moment(); // 현재시간
    let climbDiffTime = currentClimbTime.diff(climbStartTimer);
    if (climbDiffTime >= 3600000) {
      let utcFormat = moment.utc(climbDiffTime).format("HH:mm:ss");
      $(".climb_time").text(utcFormat.toString());
    } else {
      let utcFormat = moment.utc(climbDiffTime).format("mm:ss");
      $(".climb_time").text(utcFormat.toString());
    }
  }, 1000);
}

/**
 * 등산기록 저장 유틸
 */
function climbResult() {
  let climbTime = localStorage.getItem("climb.time");
  let climbName = localStorage.getItem("climb.mntiname");
  let climbDistance = localStorage.getItem("climb.distance");
  let climbLocation = localStorage.getItem("climb.location");
  let climbFinTime = moment().format("YYYY-MM-DD HH:mm:ss");
  $.ajax({
    type: "POST",
    url: "/mt/climb/result",
    async: false,
    data: {
      time: climbTime,
      name: climbName,
      distance: climbDistance,
      location: JSON.stringify(climbLocation),
      fintime: climbFinTime,
    },
    success: function (res) {
      if (res == 1) {
        localStorage.removeItem("climb.status");
        localStorage.removeItem("climb.location");
        localStorage.removeItem("climb.mntiname");
        localStorage.removeItem("climb.time");
        console.log("등산기록 저장성공");
      }
    },
    error: function (XMLHttpRequest, textStatus, errorThrown) {
      console.log("등산기록 저장실패");
    },
  });
}

function flutterNavigate(url) {
  location.replace(url);
}

/**
 * 두 좌표 거리계산
 */
function getDistance(lat1, lon1, lat2, lon2) {
  function deg2rad(deg) {
    return deg * (Math.PI / 180);
  }

  let R = 6371; // Radius of the earth in km
  let dLat = deg2rad(lat2 - lat1); // deg2rad below
  let dLon = deg2rad(lon2 - lon1);
  let a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(deg2rad(lat1)) *
      Math.cos(deg2rad(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  let result = R * c; // Distance in m
  return result;
}

/**
 * MOTION TECH "/json/likes.json"
 */
function santaLikeAnimationStart(path, speed) {
  $("#myGIF").empty();
  let anim;
  let animdata = {
    container: document.getElementById("myGIF"),
    loop: false,
    autoplay: true,
    path: path,
  };
  $(".wrapper").show();
  $("#myGIF").fadeIn(100);
  anim = bodymovin.loadAnimation(animdata);
  anim.setSpeed(speed);

  anim.addEventListener("complete", santaLikeAnimationStop);
}
function santaLikeAnimationStop() {
  $("#myGIF").fadeOut(100);
  $(".wrapper").hide();
}
// -- END

document.addEventListener("DOMContentLoaded", function () {
  /**
   * 검색창 특수문자 제거 정규식
   * @type {RegExp}
   */
  $(".search_valid").keyup(function () {
    chk_input_filter("non_spec", $(".search_valid"));
  });

  $(".search_num_valid").keyup(function () {
    chk_input_filter("number", $(".search_num_valid"));
  });

  function chk_input_filter(type, obj) {
    var str = $(obj).val();
    if (type == "alphabet") {
      //영문만 허용
      $(obj).val(str.replace(/[^a-z]/gi, ""));
    } else if (type == "number") {
      //숫자만 허용
      $(obj).val(str.replace(/[^0-9]/gi, ""));
    } else if (type == "alphabet_number") {
      //영문 , 숫자만 허용
      $(obj).val(str.replace(/[^a-z0-9]/gi, ""));
    } else if (type == "non_spec") {
      //특수문자 비허용
      $(obj).val(str.replace(/[~!@#$%^&*()_+|<>?:;{}`\-\=\\\,.'"\[\]/]/gi, ""));
    } else if (type == "name") {
      //특수문자, 숫자 비허용
      $(obj).val(
        str.replace(/[~!@#$%^&*()_+|<>?:;{}`\-\=\\\,.'"\[\]/0-9]/gi, "")
      );
    }
  }
});

/* 공통유틸내용 안에적고 외부에서 santaUtils.{function} 으로 호출 */
function SantaUtils() {
  /**
   * 입력값이  null 인지 체크한다
   */
  this.isNull = function (input) {
    if (input.value == null || input.value == "") {
      return true;
    } else {
      return false;
    }
  };

  /**
   * 입력값이 스페이스 이외의 의미있는 값이 있는지 체크한다
   * if (isEmpty(form.keyword)){
   *       Print.postMessage('값을 입력하여주세요');
   * }
   */
  this.isEmpty = function (input) {
    if (input.value == null || input.value.replace(/ /gi, "") == "") {
      return true;
    } else {
      return false;
    }
  };

  /**
   * 입력값에 특정 문자가 있는지 체크하는 로직이며
   * 특정문자를 허용하고 싶지 않을때 사용할수도 있다
   * if (containsChars(form.name, "!,*&^%$#@~;")){
   *       Print.postMessage("특수문자를 사용할수 없습니다");
   * }
   */
  this.containsChars = function (input, chars) {
    for (var i = 0; i < input.value.length; i++) {
      if (chars.indexOf(input.value.charAt(i)) != -1) {
        return true;
      }
    }
    return false;
  };

  /**
   * 입력값이 특정 문자만으로 되어있는지 체크하며
   * 특정문자만을 허용하려 할때 사용한다.
   * if (containsChars(form.name, "ABO")){
   *    Print.postMessage("혈액형 필드에는 A,B,O 문자만 사용할수 있습니다.");
   * }
   */
  this.containsCharsOnly = function (input, chars) {
    for (let i = 0; i < input.value.length; i++) {
      if (chars.indexOf(input.value.charAt(i)) == -1) {
        return false;
      }
    }
    return true;
  };

  /**
   * 입력값이 알파벳인지 체크
   * 아래 isAlphabet() 부터 isNumComma()까지의 메소드가 자주 쓰이는 경우에는
   * var chars 변수를 global 변수로 선언하고 사용하도록 한다.
   * var uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
   * var lowercase = "abcdefghijklmnopqrstuvwxyz";
   * var number = "0123456789";
   * function isAlphaNum(input){
   *       var chars = uppercase + lowercase + number;
   *    return containsCharsOnly(input, chars);
   * }
   */
  this.isAlphabet = function (input) {
    let chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입력값이 알파벳 대문자인지 체크한다
   */
  this.isUpperCase = function (input) {
    let chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입력값이 알파벳 소문자인지 체크한다
   */
  this.isLowerCase = function (input) {
    let chars = "abcdefghijklmnopqrstuvwxyz";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입력값이 숫자만 있는지 체크한다.
   */
  this.isNumer = function (input) {
    let chars = "0123456789";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입려값이 알파벳, 숫자로 되어있는지 체크한다
   */
  this.isAlphaNum = function (input) {
    let chars =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입력값이 숫자, 대시"-" 로 되어있는지 체크한다
   * 전화번호나 우편번호, 계좌번호에 -  체크할때 유용하다
   */
  this.isNumDash = function (input) {
    let chars = "-0123456789";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입력값이 숫자, 콤마',' 로 되어있는지 체크한다
   */
  this.isNumComma = function (input) {
    let chars = ",0123456789";
    return this.containsCharsOnly(input, chars);
  };

  /**
   * 입력값이 사용자가 정의한 포맷 형식인지 체크
   * 자세한 format 형식은 자바스크립트의 'reqular expression' 참고한다
   */
  this.isValidFormat = function (input, format) {
    if (input.search(format) != -1) {
      return true; // 올바른 포멧형식
    }
    return false;
  };

  /**
   * 입력값이 이메일 형식인지 체크한다
   * if (!isValidEmail(form.email)){
   *       Print.postMessage("올바른 이메일 주소가 아닙니다");
   * }
   */
  this.isValidEmail = function (input) {
    let format = /^((\w|[\-\.])+)@((\w|[\-\.])+)\.([A-Za-z]+)$/;
    return this.isValidFormat(input, format);
  };

  /**
   * 입력값이 전화번호 형식(숫자-숫자-숫자)인지 체크한다
   */
  this.isValidPhone = function (input) {
    let format = /^(\d+)-(\d+)-(\d+)$/;
    return this.isValidFormat(input, format);
  };

  /**
   * 입력값의 바이트 길이를 리턴한다.
   * if (getByteLength(form.title) > 100){
   *    Print.postMessage("제목은 한글 50자 (영문 100자) 이상 입력할수 없습니다");
   * }
   */
  this.getByteLength = function (input) {
    let byteLength = 0;
    for (let inx = 0; inx < input.value.charAt(inx); inx++) {
      let oneChar = escape(input.value.charAt(inx));
      if (oneChar.length == 1) {
        byteLength++;
      } else if (oneChar.indexOf("%u") != -1) {
        byteLength += 2;
      } else if (oneChar.indexOf("%") != -1) {
        byteLength += oneChar.length / 3;
      }
    }
    return byteLength;
  };

  /**
   * 입력값에서 콤마를 없앤다
   */
  this.removeComma = function (input) {
    return input.value.replace(/,/gi, "");
  };

  /**
   * 선택된 라디오버튼이 있는지 체크한다
   */
  this.hasCheckedRadio = function (input) {
    if (input.length > 1) {
      for (let inx = 0; inx < input.length; inx++) {
        if (input[inx].checked) return true;
      }
    } else {
      if (input.checked) return true;
    }
    return false;
  };

  /**
   * 선택된 체크박스가 있는지 체크
   */
  this.hasCheckedBox = function (input) {
    return this.hasCheckedRadio(input);
  };

  /**
   * 숫자에 천단위로 , 콤마를 찍어주는 유틸
   */
  this.returnWithComma = function (input) {
    return input.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
  };
}
var santaUtils = new SantaUtils();

/**
 * 중복클릭 FLAG
 * @type {boolean}
 */
var doubleSubmitFlag = false;

function overClick() {
  if (doubleSubmitFlag) {
    return doubleSubmitFlag;
  } else {
    doubleSubmitFlag = true;
    return false;
  }
}

/**
 * Cookie 관리
 */

function SantaCookie() {
  /**
   * CookieSet
   * @param name
   * @param value
   * @param exp 실행시간
   * EX ) setCookie(~~~~,~~~~,-1); 실행시간값을 -1로 지정시 쿠키삭제가된다. resetCookie 나 setCookie 맘대로이용
   */
  this.setCookie = function (name, value, exp) {
    var date = new Date();
    date.setTime(date.getTime() + exp * 24 * 60 * 60 * 1000);
    document.cookie =
      name + "=" + escape(value) + ";expires=" + date.toUTCString() + ";path=/";
  };

  /**
   * CookieGet
   * @param name
   * @returns {string|null}
   */
  this.getCookie = function (name) {
    var value = document.cookie.match("(^|;) ?" + name + "=([^;]*)(;|$)");
    return value ? unescape(value[2]) : null;
  };

  /**
   * CookieExpire
   * @param cName
   */
  this.resetCookie = function (cName) {
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie =
      cName + "= " + "; expires=" + expireDate.toGMTString() + "; path=/";
  };
}

var santaCookie = new SantaCookie();
