/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 1/1/14
 * Time: 12:00 AM
 *
 * This script is used as absolute js file to improve the manual. It takes care of the objects and methods that can be
 * used without risking a language distortion.
 * It should be included in the head of all manual files located here: ../../language/{language}/manual.html.
 */

//self explanatory
var imgPath = "../../manual/images"
function getImg(name){return imgPath + "/#.png".replace("#", name)}


/**
 * This method is used to toggle the given (div) chapter.
 * @param id: the trunk of the given (div) identifier.
 * @note: For this method to work properly the given div element and
 *        the img element used to invoke this method should have the same id trunk
 *        and respectively end with "Div" and "Click".
 *        For instance let's we are working on the chapter 2.1
 *        the trunk could be "chapterTwoOne", therefore the
 *        the div.id="chapterTwoOneDiv" and the img.id="chapterTwoOneClick"
 */
function toggleChapter(id){
    clickId = $("#" + id + "Click"); divId = $("#" + id + "Div"); //the elements

    //the toggle method automatically changes the status
    if(divId.css("display") == "none") clickId.attr({src: getImg("close"), alt: " - "})
    else clickId.attr({src: getImg("open"), alt: " + "})

    divId.toggle(100)
}

/**
 * This method is used to transform some specific elements before they are shown.
 * These transformations are the following:
 * - all "a" elements with a class=node => an .xml like node
 * - all "a" elements with a class=open => a self made button
 * - all "td" elements with a class=bullet => a bullet
 */
function transformClassElements(){
    /* In js (disadvantage: the if statement)
    elements = document.getElementsByTagName("a")
    for (var i=0; i < elements.length; i++){
        elem = elements[i]
        if(elem.className == "node") elem.innerHTML = "&lt;" + elem.innerHTML.trim() + "/&gt;"
        else if(elem.className == "open") elem.innerHTML = "&nbsp;" + elem.innerHTML.trim() + "&nbsp;&nbsp;"
    }
    */

    //transform all "a" elements with class=node to an .xml like node
    prefix = '<span class="node">&lt;</span>'
    suffix = '<span class="node">/&gt;&nbsp;' + nodeTerm + '</span>'
    //the nodeTerm value is set in the respective language manual.js file
    nodes = $('a[class=node]') //jquery
    nodes.each(function(i){
        node = nodes[i]
        node.innerHTML = prefix + node.innerHTML.trim() + suffix //js
    })

    //transform all "a" elements with class=open to a self made file opener with title
    refs = $('a[class=open]') //jquery
    refs.each(function(i){
        ref = refs[i]
        ref.innerHTML = "&nbsp;" + ref.innerHTML.trim() + "&nbsp;&nbsp;" //js
        ref.setAttribute("title", openTitle)
        //the openTitle value is set in the respective language manual.js file
    })

    //set the title all "input" elements with name=file
    files = $("input[name=file]")
    files.each(function(i){files[i].setAttribute("title", openTitle)})

    //transform all "td" elements with class=bullet to a bullet
    bullets = $('td[class=bullet]') //jquery
    bullets.each(function(i){bullets[i].innerHTML = "&#x95;&nbsp;"}) //the bullet (js)

}


/**
 *
 */
function openSelectedFiles(){
    var files = $("input[name=file]:checked")
    files.each(function(i){window.open(files[i].value, "_blank")})
    /*
    for(var i=0; i<fileIds.length; i++){
        id = $("#" + fileIds[i])
        if(id.is(':checked')){}
        alert(id + ": " + checked)
    }
    */
}
