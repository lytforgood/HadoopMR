package com.bs.bus;

public class Bus {
	private String use_city;// 使用地
	private String line_name;// 线路名称
	private String terminal_id;// 刷卡终端ID
	private String card_id;// 卡片ID
	private String create_city;// 发卡地
	private String deal_time;// 交易时间
	private String deal_yymmdd;// 交易时间年月日
	private String deal_hh;// 交易时间小时
	private String card_type;// 卡类型
	private boolean valid;
	private String weather;// 天气状况
	private String temperature;// 气温
	private String wind_direction_force;// 风向风力

	public static Bus parser(String log) {
		String[] array = log.split(",");
		Bus bus = new Bus();
		if (array.length == 7) {
			bus.setUse_city(array[0]);
			bus.setLine_name(array[1]);
			bus.setTerminal_id(array[2]);
			bus.setCard_id(array[3]);
			bus.setCreate_city(array[4]);
			bus.setDeal_time(array[5]);
			bus.setCard_type(array[6]);
			bus.setDeal_yymmdd((bus.getDeal_time().substring(0, bus.getDeal_time().length()-2)));
			bus.setDeal_hh((bus.getDeal_time().substring(bus.getDeal_time().length()-2, bus.getDeal_time().length())));
			if (Integer.parseInt(bus.getDeal_time().substring(bus.getDeal_time().length()-2, bus.getDeal_time().length()))<6||Integer.parseInt(bus.getDeal_time().substring(bus.getDeal_time().length()-2, bus.getDeal_time().length()))>21) {
				bus.setValid(false);
				return bus;
			}
			
			bus.setValid(true);
		}
		else {
			bus.setValid(false);
		}
		return bus;

	}

	public String getUse_city() {
		return use_city;
	}

	public void setUse_city(String use_city) {
		this.use_city = use_city;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}

	public String getCreate_city() {
		return create_city;
	}

	public void setCreate_city(String create_city) {
		this.create_city = create_city;
	}


	public String getDeal_time() {
		return deal_time;
	}

	public void setDeal_time(String deal_time) {
		this.deal_time = deal_time;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getDeal_yymmdd() {
		return deal_yymmdd;
	}

	public void setDeal_yymmdd(String deal_yymmdd) {
		this.deal_yymmdd = deal_yymmdd;
	}

	public String getDeal_hh() {
		return deal_hh;
	}

	public void setDeal_hh(String deal_hh) {
		this.deal_hh = deal_hh;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getWind_direction_force() {
		return wind_direction_force;
	}

	public void setWind_direction_force(String wind_direction_force) {
		this.wind_direction_force = wind_direction_force;
	}

}
