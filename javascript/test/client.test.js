const { Client, Config } = require('../src/client');

describe('Client', () => {
    let client;
    let config;

    beforeEach(() => {
        config = new Config();
        config.endpoint = 'http://127.0.0.1:8080';
        client = new Client(config);
    });

    test('should create client with config', () => {
        expect(client).toBeDefined();
        expect(client.config).toBeDefined();
        expect(client.config.endpoint).toBe('http://127.0.0.1:8080');
    });

    test('should create client without config', () => {
        const newClient = new Client();
        expect(newClient).toBeDefined();
        expect(newClient.config).toBeDefined();
        expect(newClient.config.endpoint).toBe('http://127.0.0.1:8080');
    });

    test('should return mock cost v2 data', async () => {
        const request = { window: 'today' };
        const response = await client.getCostV2(request);
        
        expect(response).toBeDefined();
        expect(response.code).toBe(200);
        expect(response.data).toBeDefined();
    });

    test('should return mock allocation data', async () => {
        const request = { window: 'today' };
        const response = await client.getAllocation(request);
        
        expect(response).toBeDefined();
        expect(response.code).toBe(200);
        expect(response.data).toBeDefined();
    });

    test('should return mock cost v2 data with namespace aggregation', async () => {
        const request = { 
            window: 'today',
            aggregate: 'namespace'
        };
        const response = await client.getCostV2(request);
        
        expect(response).toBeDefined();
        expect(response.code).toBe(200);
        expect(response.data).toBeDefined();
        expect(response.data.items).toBeDefined();
    });

    test('should return mock allocation data with namespace aggregation', async () => {
        const request = { 
            window: 'today',
            aggregate: 'namespace'
        };
        const response = await client.getAllocation(request);
        
        expect(response).toBeDefined();
        expect(response.code).toBe(200);
        expect(response.data).toBeDefined();
        expect(response.data.items).toBeDefined();
    });
});

describe('Config', () => {
    test('should create config with default values', () => {
        const config = new Config();
        expect(config.endpoint).toBe('http://127.0.0.1:8080');
        expect(config.accessKeyId).toBe('');
        expect(config.accessKeySecret).toBe('');
    });

    test('should allow setting endpoint', () => {
        const config = new Config();
        config.endpoint = 'http://test.endpoint.com';
        expect(config.endpoint).toBe('http://test.endpoint.com');
    });
});