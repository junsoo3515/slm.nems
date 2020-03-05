package module.object;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import module.dao.sla.SlaDao;
import module.etc.CmnEtcBiz;
import module.vo.sla.SlaResVO;

import java.util.*;

/**
 * SLA 항목 평가 함수 관련 Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 6. 13
 * Time: 오후 2:57
 */
public class CmnSLAMathBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnSLAMathBiz.class); // SLF4J Logger

    private SqlSession sqlSession;

    /**
     * SLA 항목별 평가 결과 저장소
     */
    public Map<String, SlaResVO> calcResult;


    /**
     * 초기화
     */
    public CmnSLAMathBiz() {

        calcResult = new HashMap<String, SlaResVO>(); // SLA 항목별 평가 결과 저장소
    }

    /**
     * sqlSession Setting
     *
     * @param sql Mybatis의 xml을 사용하기 위해 인자 전달
     */
    public CmnSLAMathBiz(SqlSession sql) {

        sqlSession = sql;

        if (calcResult == null) {

            calcResult = new HashMap<String, SlaResVO>(); // SLA 항목별 평가 결과 저장소
        }
    }

    /**
     * SLA 항목 평가 DAO
     *
     * @return MenuDao
     */
    public SlaDao getDao() {

        return sqlSession.getMapper(SlaDao.class);
    }

    /**
     * SLA 항목 평가 옵션
     * : DISORDERMNGRATE(장애처리적기율)
     * : DUPDISORDERNUM(중복장애건수)
     * : BACKUPSUCCESSRATE(백업성공률)
     * : SPARESECURE(예비품확보)
     * : DISORDEREXAMRATE(장애규명율)
     * : SYSEFFICIENCYMNG(시스템성능관리)
     * : BANDWIDTHUSERATE(대역폭사용률)
     * : SERVICEREQMNGRATE(서비스요청적기처리율)
     * : SECURITYACCNUM(보안사고발생건수)
     * : SITECHECKRATE(현장점검실시율)
     * : VERSIONMNG(버전관리)
     */
    public static enum calcMathOpt {
        DISORDERMNGRATE, DUPDISORDERNUM, BACKUPSUCCESSRATE, SPARESECURE, DISORDEREXAMRATE, SYSEFFICIENCYMNG, BANDWIDTHUSERATE, SERVICEREQMNGRATE, SECURITYACCNUM, SITECHECKRATE, VERSIONMNG
    }

    /**
     * SLA 항목별 평가 결과 저장소 저장
     *
     * @param itmCd     평가지표 코드
     * @param resV      측정결과
     * @param expressNm 계산식(한글)
     * @param cont      측정내용
     */
    private void setCalcResult(String itmCd, Double resV, String expressNm, String cont) {

        if (!calcResult.containsKey(itmCd)) {

            calcResult.put(itmCd, new SlaResVO());
        }

        calcResult.get(itmCd).mea_res = String.valueOf(resV);
        calcResult.get(itmCd).arith_expression_nm = expressNm;
        calcResult.get(itmCd).mea_cont = cont;
    }

    /**
     * 장애처리적기율 계산
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 계산결과 double
     */
    public Double calcDisOrderMngRate(String itmCd, List<String> arrDate) {

        Double retV = 100.0;
        String cont = null;

        try {

            Map<String, Integer> resData = getDao().getDisOrderMngData(arrDate, Collections.min(arrDate), Collections.max(arrDate));

            String allCnt = "0";
            String normalCnt = "0";

            if (resData != null) {

                allCnt = String.valueOf(resData.get("ALL_CNT"));
                normalCnt = String.valueOf(resData.get("NORMAL_CNT"));

                if (Integer.valueOf(allCnt) > 0) {

                    retV = (Double.valueOf(normalCnt) / Double.valueOf(allCnt)) * 100.0;
                }
            }

            cont = String.format("※ (%,d / %,d) × 100 =  %,.1f%s", Integer.valueOf(normalCnt), Integer.valueOf(allCnt), retV, "%");

            setCalcResult(itmCd, retV, null, cont);
            resData = null;
        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        }

        return retV;
    }

    /**
     * 중복 장애건수 계산
     * - 3건 이상 처리
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @param minCnt  최소 중복개수 이상
     * @return 계산결과 double
     */
    public Double calcDupDisOrderNum(String itmCd, List<String> arrDate, int minCnt) {

        Double retV = 0.0;

        try {

            Map<String, String> resData = getDao().getDupDisOrderNumData(arrDate, Collections.min(arrDate), Collections.max(arrDate), minCnt);

            if (resData != null) {

                retV = (double) (resData.size());
            }

            setCalcResult(itmCd, retV, null, null);
            resData = null;

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        }

        return retV;
    }

    /**
     * 백업성공률
     *
     * @param itmCd 평가지표 코드
     * @param key   SLA 고유번호
     * @return 계산결과 double
     */
    public Double calcBackupSuccessRate(String itmCd, Long key) {

        Double retV = 100.0;
        StringBuffer expressNm = new StringBuffer();
        StringBuffer cont = new StringBuffer();
        StringBuffer tmpCont = new StringBuffer();

        try {

            Map<String, Object> resData = getDao().getBackupSuccessRateData(key);

            if (resData != null) {

                String sucCnt = String.valueOf(resData.get("SUC_CNT"));
                String failCnt = String.valueOf(resData.get("FAIL_CNT"));
                String stopCnt = String.valueOf(resData.get("STOP_CNT"));
                String restorePlanCnt = String.valueOf(resData.get("RES_PLAN_CNT"));
                String restoreSucCnt = String.valueOf(resData.get("RES_SUC_CNT"));

                Double totalCnt = Double.valueOf(sucCnt) + Double.valueOf(failCnt) + Double.valueOf(stopCnt);
                Double failCal = Double.valueOf(failCnt) + Double.valueOf(stopCnt);

                cont.append("※ 성공 : ").append(Integer.valueOf(sucCnt)).append("건\n");
                cont.append("※ 중단 : ").append(Integer.valueOf(stopCnt)).append("건\n");
                cont.append("※ 실패 : ").append(Integer.valueOf(failCnt)).append("건\n");

                retV = ((totalCnt - failCal) / totalCnt) * 100.0;
                tmpCont.append(String.format("※ 산식 : (%,d / %,d)", Double.valueOf(totalCnt - failCal).intValue(), totalCnt.intValue()));

                if (Double.valueOf(restorePlanCnt) > 0.0) {

                    expressNm.append("(백업성공건수  / 백업계획건수) × (복구성공건수 / 복구계획건수)  × 100");
                    cont.append("※ 복구성공 : ").append(Integer.valueOf(restoreSucCnt)).append("건\n");
                    cont.append("※ 복구계획 : ").append(Integer.valueOf(restorePlanCnt)).append("건\n");

                    retV *= (Double.valueOf(restoreSucCnt) / Double.valueOf(restorePlanCnt));
                    tmpCont.append(String.format(" × (%,d / %,d)", Integer.valueOf(restoreSucCnt), Integer.valueOf(restorePlanCnt)));
                }

                tmpCont.append(String.format(" × 100 =  %,.1f%s", retV, "%"));

                cont.append(tmpCont.toString());

                tmpCont = null;
            }

            setCalcResult(itmCd, retV, expressNm.toString(), cont.toString());
            resData = null;

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        } finally {

            expressNm = null;
            cont = null;
        }

        return retV;
    }

    /**
     * 장애규명율
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 계산결과 double
     */
    public Double calcDisOrderExamRate(String itmCd, List<String> arrDate) {

        Double retV = 100.0;
        String cont = null;

        try {

            Map<String, Integer> resData = getDao().getDisOrderMngData(arrDate, Collections.min(arrDate), Collections.max(arrDate));

            String allCnt = "0";
            int errCnt = 0;

            if (resData != null) {

                allCnt = String.valueOf(resData.get("ALL_CNT"));
                errCnt = getDao().getDisOrderExamRateData(arrDate, Collections.min(arrDate), Collections.max(arrDate));

                if (Integer.valueOf(allCnt) > 0) {

                    retV = (Double.valueOf(errCnt) / Double.valueOf(allCnt)) * 100.0;
                }
            }

            cont = String.format("※ (%,d / %,d) × 100 =  %,.1f%s", errCnt, Integer.valueOf(allCnt), retV, "%");

            setCalcResult(itmCd, retV, null, cont);
            resData = null;

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        }

        return retV;
    }

    /**
     * 시스템 성능관리
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 계산결과 double
     */
    public Double calcSysEfficiencyMng(String itmCd, List<String> arrDate) {

        Double retV = 100.0;

        try {

            Double totalHour = getDao().getDisOrderAllHourData(arrDate, Collections.min(arrDate), Collections.max(arrDate));

            int dayCnt = arrDate.size();

            if (totalHour > 0.0) {

                retV = (((double) dayCnt * 24.0) - totalHour) / ((double) dayCnt * 24.0) * 100.0;
            }

            setCalcResult(itmCd, retV, null, String.format("※ [ { (%,d × 24) - %,d } / (%,d × 24) ] × 100 =  %,.1f%s", dayCnt, totalHour.intValue(), dayCnt, retV, "%"));

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        }

        return retV;
    }

    /**
     * 대역폭 사용률
     *
     * @param itmCd 평가지표 코드
     * @param key   SLA 고유번호
     * @return 계산결과 double
     */
    public Double calcBandWidthUseRate(String itmCd, Long key) {

        Double retV = 100.0;
        StringBuffer cont = new StringBuffer();

        try {

            List<Map<String, Object>> resData = getDao().getBandWidthUseData(key);

            if (resData != null) {

                CmnEtcBiz etcBiz = new CmnEtcBiz();
                TreeMap<String, Map<String, Object>> tmpData = new TreeMap<String, Map<String, Object>>();

                if (resData.size() > 0) {
                    // 측정결과
                    Double tmpV = Double.NaN, maxV = 0.0;

                    for (Map<String, Object> obj : resData) {

                        String tmpKey = String.format("%s_%s", obj.get("HIG_CD").toString(), obj.get("LINK_CD").toString());

                        if (!tmpData.containsKey(tmpKey)) {

                            tmpData.put(tmpKey, new HashMap<String, Object>(obj));
                        } else {

                            if (obj.get("USE_RATE") != null) {

                                if (Double.valueOf(String.valueOf(obj.get("USE_RATE"))) > Double.valueOf(String.valueOf(tmpData.get(tmpKey).get("USE_RATE")))) {

                                    tmpData.put(tmpKey, new HashMap<String, Object>(obj));
                                }
                            }
                        }

                        if (obj.get("USE_RATE") != null) {

                            tmpV = Double.valueOf(String.valueOf(obj.get("USE_RATE"))) / Double.valueOf(String.valueOf(obj.get("BAND"))) * 100.0;

                            if (tmpV > maxV) {

                                maxV = tmpV;
                            }
                        }
                    }

                    if (maxV > 0.0) {

                        retV = maxV;
                    }
                }

                // 측정내용
                String grpCd = "";
                List<String> arrGrpCd = new ArrayList<String>();
                for (Map.Entry<String, Map<String, Object>> elem : tmpData.entrySet()) {

                    grpCd = String.valueOf(elem.getValue().get("HIG_CD"));

                    if (!arrGrpCd.contains(grpCd)) {

                        if (arrGrpCd.size() > 0) {

                            cont.append("\n");
                        }

                        arrGrpCd.add(grpCd);

                        cont.append(elem.getValue().get("HIG_NM").toString()).append("\n");
                    }

                    cont.append(
                            String.format("%s : (%,.".concat(String.valueOf(elem.getValue().get("PRIME"))).concat("f %s / %,.").concat(String.valueOf(elem.getValue().get("PRIME"))).concat("f x 100 = %,.2f%s"),
                                    String.valueOf(elem.getValue().get("LINK_CD")),
                                    Double.valueOf(String.valueOf(elem.getValue().get("USE_RATE"))),
                                    String.valueOf(elem.getValue().get("CD_UNIT")),
                                    Double.valueOf(String.valueOf(elem.getValue().get("BAND"))),
                                    Double.valueOf(String.valueOf(elem.getValue().get("USE_RATE"))) / Double.valueOf(String.valueOf(elem.getValue().get("BAND"))) * 100.0,
                                    "%")
                    ).append("\n");
                }

                etcBiz = null;
            }

            setCalcResult(itmCd, retV, null, cont.toString());
            resData = null;

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        } finally {

            cont = null;
        }

        return retV;
    }

    /**
     * 서비스요청 적기 처리율
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 계산결과 double
     */
    public Double calcServiceReqMngRate(String itmCd, List<String> arrDate) {

        Double retV = 100.0;

        try {

            String allCnt = "0";
            String finCnt = "0";

            Map<String, Integer> resData = getDao().getServiceReqMngRateData(arrDate, Collections.min(arrDate), Collections.max(arrDate));

            if (resData != null) {

                allCnt = String.valueOf(resData.get("REQ_CNT"));
                finCnt = String.valueOf(resData.get("FIN_CNT"));

                if (Integer.valueOf(allCnt) > 0) {

                    retV = (Double.valueOf(finCnt) / Double.valueOf(allCnt)) * 100.0;
                }
            }

            setCalcResult(itmCd, retV, null, String.format("※ (%,d / %,d) × 100 =  %,.1f%s", Integer.valueOf(finCnt), Integer.valueOf(allCnt), retV, "%"));

            resData = null;

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        }

        return retV;
    }

    /**
     * 보안사고 발생건수
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 계산결과 double
     */
    public Double calcSecurityAccNum(String itmCd, List<String> arrDate) {

        Double retV = 1.0;

        setCalcResult(itmCd, retV, null, null);

        return retV;
    }

    /**
     * 현장점검 실시율
     *
     * @param itmCd   평가지표 코드
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 계산결과 double
     */
    public Double calcSiteCheckRate(String itmCd, List<String> arrDate) {

        Double retV = 0.0;

        try {

            String allCnt = "0";
            String proCnt = "0";
            Map<String, Integer> resData = getDao().getSiteCheckRateData(arrDate);

            if (resData != null) {

                allCnt = String.valueOf(resData.get("TOT_CNT"));
                proCnt = String.valueOf(resData.get("PRO_CNT"));

                if (Integer.valueOf(allCnt) > 0) {

                    retV = (Double.valueOf(proCnt) / Double.valueOf(allCnt)) * 100.0;
                }
            }

            setCalcResult(itmCd, retV, null, String.format("※ (%,d대/ %,d대) × 100 =  %,.1f%s", Integer.valueOf(proCnt), Integer.valueOf(allCnt), retV, "%"));
            resData = null;

        } catch (Exception ex) {

            retV = Double.NaN;
            log.error(ex.toString(), ex);
        }

        return retV;
    }
}
