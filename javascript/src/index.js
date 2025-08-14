/**
 * 阿里云ACK成本管理JavaScript SDK
 * @module ack-cost-sdk
 */

const Client = require('./client');
const Config = require('./client').Config;
const CostV2Service = require('./costv2').CostV2Service;
const CostV2Request = require('./costv2').CostV2Request;
const CostV2Response = require('./costv2').CostV2Response;
const AllocationService = require('./allocation').AllocationService;
const AllocationRequest = require('./allocation').AllocationRequest;
const AllocationResponse = require('./allocation').AllocationResponse;
const CostError = require('./errors').CostError;
const ERROR_CODES = require('./errors').ERROR_CODES;

module.exports = {
    Client,
    Config,
    CostV2Service,
    CostV2Request,
    CostV2Response,
    AllocationService,
    AllocationRequest,
    AllocationResponse,
    CostError,
    ERROR_CODES
};