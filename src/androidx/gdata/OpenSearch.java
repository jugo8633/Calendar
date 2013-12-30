//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;

/**
*
* @author jennings
* Date: Nov 12, 2008
*/
public class OpenSearch {
  private String totalResults;
  private String startIndex;
  private String itemsPerPage;

  public OpenSearch(String totalResults, String startIndex, String itemsPerPage) {
    this.totalResults = totalResults;
    this.startIndex = startIndex;
    this.itemsPerPage = itemsPerPage;
  }

  public String getTotalResults() {
    return totalResults;
  }

  public String getStartIndex() {
    return startIndex;
  }

  public String getItemsPerPage() {
    return itemsPerPage;
  }
}
