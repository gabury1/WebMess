let color = "";

    $('#colorCode').minicolors({
        format : 'hex',
        chaneDelay : 500,
        letterCase : 'lowercase',
        position:'bottom left',
        change : (hex, opacity) =>
        {
            color=hex;
            console.log('change : ' + hex);
            $('#colour').val(hex);
        }
    });



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
    let name = $("#name").val();
    let pw = $("#pw").val();
    let repw = $("#repw").val();
    let email = $("#email").val();
    let colorName = $("#colorName").val();

    $.ajax({
        url : "/user/",
        data : {
            "name" : name,
            "pw" : pw,
            "repw" : repw,
            "colorCode" : color,
            "colorName" : colorName,
            "email" : email
            },
        method : "POST",
        success : data => {
            if(data == "success") location.href = "/";
            else alert(data);
        },
        error : e => alert(e.responseText)
    })
}

function emailAuth()
{
    alert("죄송합니다~ 아직 안만들었습니다~");
    return;

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