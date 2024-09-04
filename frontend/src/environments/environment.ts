declare const API_BASE_URL: string;

export const environment = {
	production: false,
	apiUrl: API_BASE_URL || 'https://api.baccaratio.porkolab.hu',
};