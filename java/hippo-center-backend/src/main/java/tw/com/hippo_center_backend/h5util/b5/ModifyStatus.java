package tw.com.hippo_center_backend.h5util.b5;
// 修改時查詢 是否有異動，有異動+1，沒有就+0
public class ModifyStatus {
			private int status = 0;  // 初始狀態為0
		
		public void addStatus(boolean isModified) {
			if (isModified) {
				this.status += 1;
			}
		}
		public int getStatus() {
			return this.status;
		}
	}

