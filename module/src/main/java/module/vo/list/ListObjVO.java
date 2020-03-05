package module.vo.list;

/**
 * 리스트 Obj VO
 */
public class ListObjVO {
    public String id;    // 키
    public String val;    // 값
    public String text;    // 값

    /**
     * 초기화
     */
    public ListObjVO() {

    }

    /**
     * Setting
     *
     * @param k Key
     * @param v Value
     */
    public ListObjVO(String k, String v) {
        this.id = k;
        this.val = v;
        this.text = v;
    }
}
