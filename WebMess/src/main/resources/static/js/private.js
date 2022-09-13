let mode;
friendMode();

function friendMode()
{
    // 이미 친구 모드라면 안바꿔준다.
    if(mode == "friend") return;

    $.ajax({
        url : "/private/friend",
        data : {},
        method : "GET",
        success : (data) =>
        {
            mode = "friend";
            $("#app_box").html(data);
        },
        error : (e) => alert(e.responseText)
    })

}

function chatMode()
{
    // 이미 친구 모드라면 안바꿔준다.
    if(mode == "chat") return;

    $.ajax({
        url : "/private/chat",
        data : {},
        method : "GET",
        success : (data) =>
        {
            mode = "chat";
            $("#app_box").html(data);
        },
        error : (e) => alert(e.responseText)
    })


}