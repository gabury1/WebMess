var checked = false;

function authCheck()
{
    var email = $("#email").val();
    $.ajax({
        url : "/auth/auth-check",
        data : {"email" : email},
        method : "GET",
        success : data => {
            if(data == "success") signup();
            else alert(data);
        },
        error : e => alert(e.responseText)
    });
}


// 회원가입
function signup()
{
    if(checked == false)
    {
        alert("ID 체크를 해주세요.");
        return;
    }

    let id = $("#id").val();
    let pw = $("#pw").val();
    let repw = $("#repw").val();
    let name = $("#name").val();

    $.ajax({
        url : "/user/",
        data : {"id" : id, "pw" : pw, "repw" : repw, "name" : name},
        method : "POST",
        success : data => {
            if(data == "success") location.href = "/";
            else alert(data);
        },
        error : e => alert(e.responseText)
    })
}

// ID 중복확인
function idCheck()
{
    var id = $("#id").val();

    $.ajax({
        url : "/user/idcheck",
        data : {"id" : id},
        method : "GET",
        success : message => {
            if(message == "success")
            {
                $("#id").attr("disabled", true);
                checked = true;
                alert("사용가능한 ID입니다!");
            }
            else
            {
                alert("사용할 수 없는 ID입니다.");
            }
        },
        error : e => alert(e.responseText)

    })

}

function emailAuth()
{
   var email = $("#email").val();
    $.ajax({
        url : "/auth/token-request",
        data :  {"email" : email},
        method : "POST",
        success : message => {
            if(message == "success") alert("이메일이 발송되었습니다! 인증 메일을 확인해주세요.");
            else alert(message);
        },
        error : e => $("body").html(e.responseText)
    })
}