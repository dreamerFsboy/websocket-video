import axios from "axios";
import qs from "qs";
import { KMessage } from "@/methods/KMessage";
const http = axios.create({
  baseURL: "/api",
  timeout: 30000,
});

enum HttpType {
    OK= 200,
}

http.interceptors.request.use((config) => {
  // console.log(config.data)
  try {
    if ('ContentType' in config&&config.ContentType === "application/x-www-form-urlencoded") {
      config.data =
        config.data && qs.stringify(config.data, { indices: false });
    }
  } catch (e) {
    // console.log(e);
  }
  // console.log(config);
  return config;
});

http.interceptors.response.use(
  async (response) => {
    // HTTP响应状态码正常
    if (response.status === 200) {
      // nprogress.done();
      if ('code' in response.data) {
        // const store = userStore();
        const data = response.data;
        switch (data.code) {
          case HttpType.OK:
            return Promise.resolve(data);
          default:
            // 显示操作失败提示
            // ElMessage.error(response.data.message || "操作失败!!!");
            KMessage(data.msg,'warning')
            return Promise.reject(data);
        }
      }
    //   sayError("请确认您的后端包装是否正确")
      KMessage("请确认您的后端包装是否正确",'danger')
      return Promise.reject(response);
    } else {
    //   sayError("连接不到服务器")
      KMessage("连接不到服务器",'danger')
      return Promise.reject(response);
    }
  },
  (error) => {
    if (error.code === 'ECONNABORTED' || error.code === 'ERR_NETWORK')
    KMessage("连接不到服务器",'danger')
    return Promise.reject(error);
  }
);



export default function (
    method: 'post'|'get'|'put'|'delete', 
    url: string, 
    submitData?: any, 
    ContentType?: 'form'|'file'|'files'|'rest'|'json') {
  let file,contentType:string
  switch (ContentType) {
    case "form":
      contentType = "application/x-www-form-urlencoded";
      break;
    case "file":
      contentType = "multipart/form-data";
      file = new FormData();
      file.append('imgFile',submitData as Blob);
      submitData = file;
      break;
    case "files":
        contentType = "multipart/form-data";
      file = new FormData();
      for(let i:number = 0;i<submitData.length;i++) {
        file.append('file',submitData[i]);
      }
      submitData = file;
      break;
    case "rest":
      url+='/'+submitData;
      submitData=null;
      break;
    default:
        contentType = "application/json";
  }
  return new Promise((resolve, reject) => {
    const reqParams = {
      method,
      url,
      [method.toLowerCase() === "get" //|| method.toLowerCase() === "delete"
        ? "params"
        : "data"]: submitData,
        contentType,
      // paramsSerializer: function (params) {
      //   return qs.stringify(params, { indices: false });
      // },
    };
    http(reqParams)
      .then((res) => {
        resolve(res.data||res);
      })
      .catch((err) => {
        // console.log(err);
        reject(err);
      });
  });
}
