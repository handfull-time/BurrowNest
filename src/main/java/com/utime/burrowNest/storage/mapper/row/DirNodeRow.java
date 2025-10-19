package com.utime.burrowNest.storage.mapper.row;

import org.apache.ibatis.type.Alias;

/**
 * 디렉터리 트리/목록 조회용 경량 프로젝션.
 * - root의 경우 parentNo가 null일 수 있습니다.
 * - hasChild는 쿼리에서 EXISTS(...)로 계산하여 매핑합니다.
 */
@Alias("DirNodeRow") // resultType="DirNodeRow" 로 사용할 수 있게 별칭 제공 (선택)
public class DirNodeRow {

    private long no;          // BN_DIRECTORY.NO
    private String uid;       // BN_DIRECTORY.UID (UUID 문자열)
    private String name;      // BN_DIRECTORY.NAME
    private Long parentNo;    // BN_DIRECTORY.PARENT_NO (root면 null)
    private boolean hasChild; // 자식 존재 여부 (EXISTS 서브쿼리 결과)

    public DirNodeRow() { }

    public DirNodeRow(long no, String uid, String name, Long parentNo, boolean hasChild) {
        this.no = no;
        this.uid = uid;
        this.name = name;
        this.parentNo = parentNo;
        this.hasChild = hasChild;
    }

    public long getNo() { return no; }
    public void setNo(long no) { this.no = no; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentNo() { return parentNo; }
    public void setParentNo(Long parentNo) { this.parentNo = parentNo; }

    public boolean isHasChild() { return hasChild; }
    public void setHasChild(boolean hasChild) { this.hasChild = hasChild; }

    @Override
    public String toString() {
        return "DirNodeRow{" +
               "no=" + no +
               ", uid='" + uid + '\'' +
               ", name='" + name + '\'' +
               ", parentNo=" + parentNo +
               ", hasChild=" + hasChild +
               '}';
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(no);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirNodeRow)) return false;
        DirNodeRow that = (DirNodeRow) o;
        return no == that.no &&
               (uid != null ? uid.equals(that.uid) : that.uid == null);
    }
}
