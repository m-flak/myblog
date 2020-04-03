import React from 'react';
import * as datetime from 'node-datetime';

export class PostDate extends React.Component {
    constructor(props) {
        super(props);

        var actualDate;
        if (this.props.date === '') {
            actualDate = datetime.create(Date.now());
        }
        else {
            actualDate = datetime.create(this.props.date);
        }

        var splitDate = actualDate.format('f D, Y I:M p').split(' ');

        splitDate.splice(3, 0, 'at');

        this.formattedDate = splitDate.join(' ');
    }

    render() {
        const {tag: Tag} = this.props;

        return (
            <Tag>{this.formattedDate}</Tag>
        );
    }
}
PostDate.defaultProps = {
    tag: 'h5',
}
