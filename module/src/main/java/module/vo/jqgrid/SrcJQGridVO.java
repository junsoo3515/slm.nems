package module.vo.jqgrid;

import org.apache.commons.lang3.StringUtils;

/**
 * jqGrid Plugin 검색 인자 VO
 * <p/>
 * jqGrid Plugin 자체에서 검색 절 넘기는 패턴이 정형화 되어 있기 때문에 사용
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:24
 */
public class SrcJQGridVO {

    /**
     * jqGrid 검색 옵션
     */
    public enum SRCOPT {
        EQ, NE, LT, LE, GT, GE, BW, BN, IN, NI, EW, EN, CN, NC, NU, NN;
    }
    public String sortID; // 소팅 헤더 아이디
    public String sortDesc; // 소팅(asc / desc)

    public boolean isSearch; // 검색 상태 유무
    public String srcField; // 검색어 필드 아이디
    public String srcStr; // 검색어 값
    public String srcOper; // 검색어 조건
    public String filters; // 필터(Model로 컨버팅 하기 위한 기타 조건들..)

    public int page; // 현재 페이지 No
    public int rows; // 표출 Row 수

    public String defWhere; // searchField, searchString, searchOper 값을 조합해서 만듬
    /**
     * jqGrid 검색 명령어 DB Where절로 변경
     *
     * @param col  칼럼명
     * @param oper 연산자
     * @param val  값
     */
    public SrcJQGridVO(String sidx, String sord, int rows, boolean _search, String col, String val, String oper, String filters, int page) {

        this.sortID = sidx;
        this.sortDesc = sord;
        this.isSearch = _search;
        this.srcField = col;
        this.srcStr = val;
        this.srcOper = oper;
        this.filters = filters;
        this.page = page;
        this.rows = rows;

        // defWhere 값 생성
        String retV = null;

        if (StringUtils.isNotEmpty(val) || StringUtils.isNotEmpty(oper)) {

            String operation = "";

            switch (SRCOPT.valueOf(this.srcOper.toUpperCase())) {
                case EQ:
                    operation = "=";
                    break;
                case NE:
                    operation = "!=";
                    break;
                case LT:
                    operation = "<";
                    break;
                case LE:
                    operation = "<=";
                    break;
                case GT:
                    operation = ">";
                    break;
                case GE:
                    operation = ">=";
                    break;
                case BW:
                case IN:
                case EW:
                case CN:
                    operation = "LIKE";
                    break;
                case BN:
                case NI:
                case EN:
                case NC:
                    operation = "NOT LIKE";
                    break;
                case NU:
                    operation = "IS NULL";
                    break;
                case NN:
                    operation = "IS NOT NULL";
                    break;
            }


            if (this.srcOper == "nu" || this.srcOper == "nn") {
                retV = this.srcField + " " + operation;
            } else {
                switch (SRCOPT.valueOf(this.srcOper.toUpperCase())) {
                    case BW:
                    case BN:
                        val += "%";
                        break;
                    case EW:
                    case EN:
                        val = "%" + val;
                        break;
                    case CN:
                    case NC:
                    case IN:
                    case NI:
                        val = "%" + val + "%";
                        break;
                }

                retV = this.srcField + " " + operation + " '" + val + "'";
            }
        }

        this.defWhere = retV;
    }
}
