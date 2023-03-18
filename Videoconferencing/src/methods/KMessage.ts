
const app = document.documentElement;
const messageArr: HTMLDivElement[] = [];
/**
 * 
 * @param {string} message 
 * @param {"primary" | "success" | "info"| "danger"|"warning"} type 
 * @param {number} time 
 */
// window.KMsg = KMessage

export function KMessage(message: string, type: "primary" | "success" | "info"| "danger"|"warning" = 'primary', time: number = 3000) {
  let el = document.createElement("div");
  el.className = "kmsg-" + type + " k-message color-btn";
  el.innerText = message;
  app.appendChild(el);
  messageArr.unshift(el);
  messageArr.forEach((v, i, a) => {
    if (i > 0) {
      v.style.top = a[i].offsetHeight * i + 15 * i + 20 + "px";
    }
  });
  let begin = setTimeout(() => {
    el.style.opacity = '1';
    begin && clearTimeout(begin);
  }, 200);

  let end = setTimeout(() => {
    el.style.opacity = '0';
    el.style.transform = "translate(-50%,100%)";
    end && clearTimeout(end);
  }, time - 500);

  let timo = setTimeout(() => {
    app.removeChild(el);
    let num = messageArr.indexOf(el);
    messageArr.splice(num, 1);
    // el = null;
    timo && clearTimeout(timo);
  }, time);
}
