package com.example.xuebshe.pojo;

import lombok.Data;

@Data
public class Roomnum {
    Integer num;
    String email;
    String nickname;
    Integer roomid;
    boolean owner;
    String avatar;

    /**
     * 参考：https://blog.csdn.net/fjian123/article/details/79534586 重写比较函数
     * 重写哈希值，每个对象的哈希值是不同的，但是可以根据种类加类型计算哈希值
     */
    @Override
    public int hashCode() {
        return email.hashCode();
    }
    /**
     * 重写比较函数，为HashList做准备
     * */
    @Override
    public boolean equals(Object o) {
        //如果两个对象完全相同？这个，自己比自己的验证吗
        if(this == o){
            return true;
        }
        //防止输入一个空值（避免下面验证出错）
        if(o == null){
            return false;
        }
        //判断是否为相同类，不是，那么返回false
        if(getClass() != o.getClass()){
            return false;
        }
        //判断上述完毕，转换对象
        Roomnum card = (Roomnum) o;
        //判断Kind是否相同
        if(email == null){
            //如果对面的不是空的
            if(card.email !=null){
                //则不相同
                return false;
            }
            //如果不是空的比较字符串
        }else{
            if(!email.equals(card.email)){
                return false;
            }
        }
        //去除判定
        return true;
    }




}


