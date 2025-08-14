class AllocationRequest {
    constructor() {
        this.window = '';
        this.filter = '';
        this.aggregate = '';
    }
    
    toObject() {
        const obj = {};
        if (this.window) obj.window = this.window;
        if (this.filter) obj.filter = this.filter;
        if (this.aggregate) obj.aggregate = this.aggregate;
        return obj;
    }
}

module.exports = {
    AllocationRequest
};