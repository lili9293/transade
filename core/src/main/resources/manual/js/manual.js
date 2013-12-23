
/**
 *
 */
var imgPath = "../../manual/images"
function getImg(name){return imgPath + "/#.png".replace("#", name)}

/**
 *
 */
function showChapter(id){
    clickId = $("#" + id + "Click"); divId = $("#" + id + "Div"); //the elements
    dis = "display"; none = "none"; status =  divId.css(dis); speed = 100;

    if(status == none){
        divId.show(speed)
        divId.css(dis, "block")
        clickId.attr({src: getImg("close"), alt: " - "})
    }else{
        divId.hide(speed)
        divId.css(dis, none)
        clickId.attr({src: getImg("open"), alt: " + "})
    }
}

/**
 * @note This method is written entirely in js.
 */
function adaptAElementInnerHTML(){
    elements = document.getElementsByTagName("a")
    for (var i=0; i < elements.length; i++){
        elem = elements[i]
        if(elem.className == "node") elem.innerHTML = "&lt;" + elem.innerHTML.trim() + "/&gt;"
        else if(elem.className == "open") elem.innerHTML = "&nbsp;" + elem.innerHTML.trim() + "&nbsp;&nbsp;"
    }
}

/**
 * @note This method is written entirely in js.
 */
function addBulletToTd(){
    elements = document.getElementsByTagName("td")
    for (var i=0; i < elements.length; i++){
        elem = elements[i]
        if(elem.className == "bullet") elem.innerHTML = "&#x95;&nbsp;" //the bullet
    }
}
