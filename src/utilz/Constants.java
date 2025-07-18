package utilz;

public class Constants {
	public static class Directions {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}
	
	public static class PlayerConstants {
		public static final int IDLE = 0;
		public static final int JUMP = 1;
		public static final int RUNNING = 2;
		public static final int DEATH = 3;
		public static final int ATACKING = 4;
		
		public static int GetSpriteAmount(int player_action) {
			switch (player_action) {
				case IDLE:
					return 1;  
				case JUMP:
					return 4;  
				case RUNNING:
					return 5; 
				case DEATH:
					return 1;
				case ATACKING:
					return 2;
				default:
					return 1;
			}
		}
	}
	
	public static class BalloonConstants {
		public static final int BIG = 0;
		public static final int MID = 1;
		public static final int SMALL = 2;
		public static final int XSMALL = 3;
		
		public static int GetSpriteAmount(int balloonaction) {
			switch (balloonaction) {
				case BIG:  
				case MID:  
				case SMALL:
				case XSMALL:
					return 4;
				default:
					return 1;
			}
		}
	}
}
