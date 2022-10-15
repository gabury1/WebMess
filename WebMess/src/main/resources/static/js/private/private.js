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
    getFriend();
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

function getFriend()
{
    $.ajax({
        url : "/user/relationList",
        data : {},
        method : "GET",
        success : (data) => {
                data = JSON.parse(data);

                // 유저리스트를 JSON으로 바꿔준다.
                var html = "";
                var online = data.onlineUser;           
                
                for(var i = 0; i < online.length; i++)
                {

                    html += 
                        '<div class="userContainer">'+
                        '                <a class="userName" style="color:' + online[i].colorCode + '" href="/user/' + online[i].no + '">' + online[i].name + '</a>'+
                        '                <div class="limeLed"></div> <div style="display:inline-block; min-width:60%"></div> <button>채팅</button><br><br>'+ 
                        '                <div class="introduce">' + online[i].introduce + '</div>'+
                        '</div>';
                }

                var offline = data.offlineUser;
                for(var i = 0; i < offline.length; i++)
                {
                    html +=  
                    '<div class="userContainer">'+
                    '                <a class="userName" style="color:' + offline[i].colorCode + '" href="/user/' + offline[i].no + '">' + offline[i].name + '</a>'+
                    '                <div class="redLed"></div> <div style="display:inline-block; min-width:60%"></div> <button>채팅</button><br><br>'+ 
                    '                <div class="introduce">' + offline[i].introduce + '</div>'+
                    '</div>';

                }

                $("#friend_box").html(html);

        },
        error : (e) => alert(e.responseText)

    });


}